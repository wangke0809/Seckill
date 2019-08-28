package com.bytecamp.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.bytecamp.biz.dto.OrderDto;
import com.bytecamp.biz.dto.PayDto;
import com.bytecamp.biz.dto.TokenDto;
import com.bytecamp.biz.enums.OrderStatusEnum;
import com.bytecamp.biz.service.*;
import com.bytecamp.biz.util.RedisKeyUtil;
import com.bytecamp.dao.OrderMapper;
import com.bytecamp.model.Product;
import com.bytecamp.util.GenerateIDUtil;
import com.bytecamp.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @Value("${seckill.machine-id}")
    private int machineId;

    @Resource
    ProductService productService;

    @Resource
    RedisService redisService;

    @Resource
    RedisLuaHelper redisLuaHelper;

    @Value("${seckill.token.url}")
    String tokenUrl;

    // 内存存储库存状态，如果为真表示库存售罄
    private HashMap<Long, Boolean> stockHashMap = new HashMap<>(5000);

    /**
     * 插入订单数据
     *
     * @param uid
     * @param pid
     * @return 订单号
     */
    @Override
    public String addOrder(Integer uid, Long pid) {

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

        // redis 中判断是否有库存
        // 用 hashmap 是为了方便实现 reset 接口

        // 库存 key
        String key = String.format(RedisKeyUtil.STOCK, productId);

        // uid 是否购买 pid key， 解决是否重复下单
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

        // 库存足够，进行下单！
        // 通过 mq 异步下单
        String orderId = GenerateIDUtil.getInstance(machineId).nextId(pid);

        String orderKey = String.format(RedisKeyUtil.ORDER, orderId);
        OrderDto order = new OrderDto();
        order.setId(orderId);
        order.setUid(uid);
        order.setPid(product.getId());
        order.setDetail(product.getDetail());
        order.setPrice(product.getPrice());
        order.setOrderStatus(OrderStatusEnum.UNFINISH.getValue());
        order.setToken(null);
        redisService.set(orderKey, JSON.toJSONString(order));

        String userOrders = String.format(RedisKeyUtil.USERORDER, uid);

        redisService.lpush(userOrders, orderId);

        log.info("下单完成 orderId {} uid {} pid {}", orderId, uid, pid);

        return orderId;


    }

    /**
     * 订单支付
     *
     * @param orderId
     * @return
     */
    @Override
    public String payOrder(String orderId, Integer uid, Integer price) {

        if (StringUtils.isEmpty(orderId) || price == null
                || price < 0 || uid == null || uid < 0) {
            return null;
        }

        OrderDto order = null;

        String orderKey = String.format(RedisKeyUtil.ORDER, orderId);
        String orderString = redisService.get(orderKey);

        if (orderString != null) {
            try {
                order = JSON.parseObject(orderString, OrderDto.class);
            } catch (Exception e) {
                log.error("反序列化订单失败");
            }
        } else {
            log.info("订单不存在");
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

        payDto.setOrderId(orderId);
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
            return null;
        }

        log.info("[ payOrder ] 成功获取 token {}", tokenDto.getToken());

        order.setOrderStatus(OrderStatusEnum.FINISH.getValue());
        order.setToken(tokenDto.getToken());

        redisService.set(orderKey, JSON.toJSONString(order));

        return tokenDto.getToken();

    }

    /**
     * 查询所有订单
     *
     * @return
     */
    @Override
    public List<OrderDto> getAllOrders(Integer uid) {
        List<OrderDto> list = new ArrayList<>();
        String userOrders = String.format(RedisKeyUtil.USERORDER, uid);
        List<String> orderOds = redisService.lrange(userOrders, 0, -1);
        for (String orderId : orderOds) {
            try {
                String orderKey = String.format(RedisKeyUtil.ORDER, orderId);
                String orderString = redisService.get(orderKey);
                OrderDto order = JSON.parseObject(orderString, OrderDto.class);
                list.add(order);
            } catch (Exception e) {
                log.error("[getAllOrders] 异常", e);
            }
        }
        return list;
    }

    /**
     * 订单回滚
     * 存入数据库之前发生任何异常需要回滚
     * 回滚 Redis 库存
     * 回滚库存内存状态
     * 回滚 Redis 已经购买状态
     *
     * @param uid
     * @param productId
     */
    public void orderRollBack(Integer uid, Long productId, String orderid) {

        log.info("[订单回滚] 开始");

        // 库存 key
        String key = String.format(RedisKeyUtil.STOCK, productId);

        // uid 是否购买 pid key， 解决是否重复下单
        String uidPidKey = String.format(RedisKeyUtil.USERPRODUCT, uid, productId);

        redisLuaHelper.stockIncr(key);
        redisService.del(uidPidKey);

        // 存在并发竞争，不过没关系
        stockHashMap.put(productId, false);

        if (orderid != null) {
            String orderKey = String.format(RedisKeyUtil.ORDER, orderid);
            String token = redisService.get(orderKey);
            // 插入数据库失败但是已经支付了
            if (!token.equals("0")) {
                log.error("[严重错误] 订单 {} 保存失败但是已经支付 {}", orderid, token);
            } else {
                redisService.del(orderKey);
            }
        }


        log.info("[订单回滚] 结束");

    }

    /**
     * 情况 hashmap
     */
    @Override
    public void stockMapClear() {
        stockHashMap.clear();
    }
}
