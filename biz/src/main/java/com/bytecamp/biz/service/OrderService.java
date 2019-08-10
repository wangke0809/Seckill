package com.bytecamp.biz.service;

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
    String addOrder(Integer uid, Integer pid);

    /**
     * 订单支付
     *
     * @param orderId
     * @return
     */
    String payOrder(String orderId);

    /**
     * 查询所有订单
     *
     * @return
     */
    List<Order> getAllOrders();

    /**
     * 删除所有
     * @return
     */
    Boolean delAllOrders();

    /**
     * 是否存在 uid 购买 pid
     * @param uid
     * @param pid
     * @return
     */
    Boolean orderExists(Integer uid, Integer pid);
}