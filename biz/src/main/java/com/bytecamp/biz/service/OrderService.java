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
     * @param detail
     * @param price
     * @return 订单号
     */
    String addOrder(int uid, int pid, String detail, Integer price);

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
}
