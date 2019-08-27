package com.bytecamp.biz.service;

import com.bytecamp.biz.dto.OrderDto;
import com.bytecamp.model.Order;

import java.util.List;

/**
 * @author wangke
 * @description: 订单服务
 * @date 2019-08-08 02:00
 */
public interface OrderService {

    /**
     * 插入订单数据
     *
     * @param uid
     * @param pid
     * @return 订单号
     */
    String addOrder(Integer uid, Long pid);

    /**
     * 订单支付
     *
     * @param orderId
     * @return
     */
    String payOrder(String orderId, Integer uid, Integer price);

    /**
     * 查询所有订单
     *
     * @return
     */
    List<OrderDto> getAllOrders(Integer uid);

    /**
     * 情况 hashmap
     */
    void stockMapClear();

}
