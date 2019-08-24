package com.bytecamp.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.bytecamp.biz.dto.OrderDto;
import com.bytecamp.biz.dto.PayDto;
import com.bytecamp.biz.dto.TokenDto;
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
import com.bytecamp.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Value("${seckill.token.url}")
    String tokenUrl;

    // 内存存储库存状态
    private HashMap<Long, Boolean> stockHashMap = new HashMap<>(2000);

    /**
     * 插入订单数据
     *
     * @param uid
     * @param pid
     * @return 订单号
     */
    @Override
    public Long addOrder(Integer uid, Long pid) {

        Product product = null;

        try {
            product = productService.getProductById(pid);
            log.info("[ addOrder] 获取商品成功 {} ", pid);
        } catch (Exception e) {
            log.error("[ addOrder] 获取商品失败 {} ", pid);
        }

        // 商品是否存在
        if (product == null) {
            log.info("[ addOrder] 商品 null");
            return null;
        }

        Long productId = product.getId();

        // 系统内存中判断是否有库存
        // 考虑集群环境，某个商品在另一个节点内存中存储时，本节点依然无法使用内存变量判断
        // 仍然往下走流程，在 redis 层判断
        if (stockHashMap.keySet().contains(productId)
                && stockHashMap.get(productId)) {
            log.info("[ addOrder]  {} hashmap 中判断已经售罄", pid);
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

        // 判断是否已经购买
        if (redisService.set(uidPidKey, "1", "NX") == null) {
            log.info("[ addOrder ] uid {} pid {} 重复下单", uid, productId);
            return null;
        }

        // 商品库存是否存储在 redis 中
        if (!stockHashMap.keySet().contains(productId)) {
            redisService.set(key, product.getCount().toString(), "NX");
            stockHashMap.put(productId, false);
        }

        // 减库存，判断库存是否足够
        Long stock = redisService.decr(key);

        if (stock == null) {
            log.error("[ addOrder] 从 redis 获取库存信息失败");
            // 删除 uid 与 pid 锁定关系
            redisService.del(uidPidKey);
            return null;
        }

        // 库存不足够
        if (stock < 0) {
            // 记录到内存
            stockHashMap.put(productId, true);
            // 删除 uid 与 pid 锁定关系
            redisService.del(uidPidKey);
            log.info("[ addOrder ] {} 库存不够，商品售罄", uidPidKey);
            return null;
        }

        // 是否重复购买涉及并发判断，不能使用 mysql 判断，使用 redis 判断
        // 而且应该现判断
        // 如果后判断，先减少再增加，库存已经减少时，新来的请求会认为没有库存
        // if(orderExists(uid, productId)){
        //     return null;
        // }

        // 库存足够，进行下单！
        // 通过 mq 异步下单
        // TODO: gen order id by time and pid
        Long orderId = 1122333L;

        OrderDto orderDto = new OrderDto();

        orderDto.setId(orderId);
        orderDto.setProduct(product);
        orderDto.setUid(uid);

        log.info("异步下单 orderId {} uid {} pid {}", orderId, uid, pid);

        try {
            mqService.sendMessageToQueue(orderDto);
            return orderId;
        } catch (Exception e) {
            // TODO: 恢复锁定的库存
            // 如果恢复，需要考虑当库存为负数时的情况，考虑到负数和集群
            // 需要另一个字段判断是否有库存，有再减库存，查询库存是请求两次 redis

            // 删除 uid 与 pid 锁定关系
            redisService.del(uidPidKey);
            return null;
        }

    }

    /**
     * 订单支付
     *
     * @param orderId
     * @return
     */
    @Override
    public String payOrder(Long orderId, Integer uid, Integer price) {

        if (StringUtils.isEmpty(orderId) || price == null
                || price < 0 || uid == null || uid < 0) {
            return null;
        }

        Order order;

        try {
            order = _mapper.selectByPrimaryKey(orderId);
        } catch (Exception e) {
            log.error("[ payOrder ] 查询订单异常 {}", orderId, e);
            return null;
        }

        if (order == null) {
            log.error("[ payOrder ] 查询订单失败 {}", orderId);
            return null;
        }

        if (order.getOrderStatus().equals((Byte) OrderStatusEnum.FINISH.getValue())) {
            log.error("[ payOrder ] 已经支付 {}", orderId);
            return null;
        }

        if (order.getUid() != null && !order.getUid().equals(uid)) {
            log.error("[ payOrder ] 支付用户不一致 {}", orderId);
            return null;
        }

        if (!price.equals(order.getPrice())) {
            log.error("[ payOrder ] 支付金额不一致 {}", orderId);
            return null;
        }

        PayDto payDto = new PayDto();

        payDto.setOrderId(orderId.toString());
        payDto.setPrice(order.getPrice());
        payDto.setUid(order.getUid());

        TokenDto tokenDto = null;

        try {
            String token = HttpUtil.postJson(tokenUrl, payDto);
            tokenDto = JSON.parseObject(token, TokenDto.class);

        } catch (Exception e) {
            // TODO: 增加重试机制
            log.error("[ payOrder ] 获取 token 异常 {}", orderId, e);
            return null;
        }

        if (tokenDto == null || tokenDto.getToken() == null) {
            log.error("[ payOrder ] 获取 token 失败 {}", orderId);
        }

        log.info("[ payOrder ] 成功获取 token {}", tokenDto.getToken());

        order.setOrderStatus(OrderStatusEnum.FINISH.getValue());
        order.setToken(tokenDto.getToken());

        try {
            if (_mapper.updateByPrimaryKey(order) > 0) {
                log.info("[ payOrder ] 订单更新成功 {}", order.getId());
                return tokenDto.getToken();
            } else {
                log.error("[ payOrder ] 订单更新失败 {}", order.getId());
                return null;
            }
        } catch (Exception e) {
            log.error("[ payOrder ] 订单更新异常 {}", order.getId(), e);
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

            OrderDto orderDto = JSON.parseObject(str, OrderDto.class);

            log.info("接收到订单 orderId {}", orderDto.getId());

            Product product = orderDto.getProduct();

            if (product == null) {
                return;
            }

            Order order = new Order();


            order.setId(orderDto.getId());
            order.setUid(orderDto.getUid());
            order.setPid(orderDto.getProduct().getId());
            order.setDetail(product.getDetail());
            order.setPrice(product.getPrice());
            order.setOrderStatus(OrderStatusEnum.UNFINISH.getValue());
            order.setToken(null);

            if (_mapper.insert(order) > 0) {
                log.info("{} 异步下单成功", orderDto.getId());
            } else {
                // TODO: 异常如何回退
                log.error("{} 异步下单失败", orderDto.getId());
            }
        } catch (Exception e) {
            // TODO: 异常如何回退
            log.error("消息队列订阅异常 {}", textMessage, e);
        }

    }
}
