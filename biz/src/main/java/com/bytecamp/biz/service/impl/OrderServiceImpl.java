package com.bytecamp.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.bytecamp.biz.dto.OrderDto;
import com.bytecamp.biz.enums.OrderStatusEnum;
import com.bytecamp.biz.service.MQService;
import com.bytecamp.biz.service.OrderService;
import com.bytecamp.biz.service.ProductService;
import com.bytecamp.biz.service.RedisService;
import com.bytecamp.biz.util.RedisKeyUtil;
import com.bytecamp.dao.OrderMapper;
import com.bytecamp.model.Order;
import com.bytecamp.model.OrderSearch;
import com.bytecamp.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author wangke
 * @description: 订单实现
 * @date 2019-08-08 02:01
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource(name = "orderMapper")
    private OrderMapper _mapper;

    @Resource
    ProductService productService;

    @Resource
    MQService mqService;

    @Resource
    RedisService redisService;

    // 内存存储库存状态
    private HashMap<Integer, Boolean> stockHashMap = new HashMap<>(2000);

    /**
     * 插入订单数据
     *
     * @param uid
     * @param pid
     * @return 订单号
     */
    @Override
    public String addOrder(Integer uid, Integer pid) {

        Product product = productService.getProductById(pid);

        // 商品是否存在
        if (product == null) {
            return null;
        }

        Integer productId = product.getId();

        // 系统内存中判断是否有库存
        // 考虑集群环境，某个商品在另一个节点内存中存储时，本节点依然无法使用内存变量判断
        // 仍然往下走流程，在 redis 层判断
        if (stockHashMap.keySet().contains(productId)
                && stockHashMap.get(productId)) {

            return null;
        }

        // redis中判断是否有库存
        // 用 hashmap 是为了方便实现 reset 接口

        // 库存 key
        String key = String.format(RedisKeyUtil.STOCK, productId);

        // uid 是否购买 pid key
        String uidPidKey = String.format(RedisKeyUtil.USERPRODUCT, uid, productId);

        // 判断是否已经购买
        // if (redisService.exists(uidPidKey)) {
        //    return null;
        // }
        if (redisService.set(uidPidKey, "1", "NX", "EX", 9999) == null) {
            return null;
        }

        // 商品库存是否存储在 redis 中
        if (!redisService.exists(key)) {
            redisService.set(key, product.getCount().toString());
        }

        // 减库存，判断库存是否足够
        Long stock = redisService.decr(key);

        // 库存不足够
        if (stock < 0) {
            // 记录到内存
            stockHashMap.put(productId, true);
            return null;
        }

        // 是否重复购买涉及并发判断，不能使用 mysql 判断，使用 redis 判断
        // 而且应该现判断
        // 如果后判断，先减少再增加，库存已经减少时，新来的请求会认为没有库存
        // if(orderExists(uid, productId)){
        //     return null;
        // }

        // 库存足够，进行下单！

        // uid 购买过 pid
        redisService.set(uidPidKey, "1");

        // 通过 mq 异步下单
        // TODO: gen order id by time and pid
        String orderId = UUID.randomUUID().toString().substring(0, 25);

        OrderDto orderDto = new OrderDto();

        orderDto.setId(orderId);
        orderDto.setPid(productId);
        orderDto.setUid(uid);

        log.info("发送异步下单 {}", JSON.toJSONString(orderDto));

        mqService.sendMessageToQueue(orderDto);

        return orderId;
    }

    /**
     * 订单支付
     *
     * @param orderId
     * @return
     */
    @Override
    public String payOrder(String orderId) {
        Order order = _mapper.selectByPrimaryKey(orderId);

        // TODO: get token
        String token = "xx";
        order.setOrderStatus(OrderStatusEnum.FINISH.getValue());
        order.setToken(token);

        if (_mapper.updateByPrimaryKey(order) > 0) {
            return token;
        } else {
            return null;
        }
    }

    /**
     * 查询所有订单
     *
     * @return
     */
    @Override
    public List<Order> getAllOrders() {
        OrderSearch search = new OrderSearch();
        List<Order> list = _mapper.selectByExample(search);
        return list;
    }

    /**
     * 删除所有
     *
     * @return
     */
    @Override
    public Boolean delAllOrders() {
        OrderSearch search = new OrderSearch();
        _mapper.deleteByExample(search);
        return true;
    }

    /**
     * 是否存在 uid 购买 pid
     *
     * @param uid
     * @param pid
     */
    @Override
    public Boolean orderExists(Integer uid, Integer pid) {
        OrderSearch search = new OrderSearch();
        search.createCriteria().andUidEqualTo(uid).andPidEqualTo(pid);
        List<Order> list = _mapper.selectByExample(search);
        if (list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 接受点对点订单队列消息
     *
     * @param message
     */
    @JmsListener(destination = "order", containerFactory = "queueListenerFactory")
    public void receiveOrderMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String str = textMessage.getText();
            log.info("接收到订单 {}", str);

            OrderDto orderDto = JSON.parseObject(str, OrderDto.class);

            Product product = productService.getProductById(orderDto.getPid());

            if (product == null) {
                return;
            }

            Order order = new Order();


            order.setId(orderDto.getId());
            order.setUid(orderDto.getUid());
            order.setPid(orderDto.getPid());
            order.setDetail(product.getDetail());
            order.setPrice(product.getPrice());
            order.setOrderStatus(OrderStatusEnum.UNFINISH.getValue());
            order.setToken(null);

            if (_mapper.insert(order) > 0) {
                log.info("{} 异步下单成功", orderDto.getId());
            } else {
                log.error("{} 异步下单失败", orderDto.getId());
            }
        } catch (Exception e) {
            // TODO: 异常如何回退
            log.error("消息队列订阅异常 {}", textMessage, e);
        }

    }
}
