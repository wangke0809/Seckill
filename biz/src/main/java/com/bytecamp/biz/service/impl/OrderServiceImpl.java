package com.bytecamp.biz.service.impl;

import com.bytecamp.biz.enums.OrderStatusEnum;
import com.bytecamp.biz.service.OrderService;
import com.bytecamp.dao.OrderMapper;
import com.bytecamp.model.Order;
import com.bytecamp.model.OrderSearch;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangke
 * @description: 订单实现
 * @date 2019-08-08 02:01
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource(name = "orderMapper")
    private OrderMapper _mapper;

    /**
     * 插入订单数据
     *
     * @param uid
     * @param pid
     * @param detail
     * @param price
     * @return 订单号
     */
    @Override
    public String addOrder(int uid, int pid, String detail, Integer price) {

        Order order = new Order();

        // TODO: gen order id by time and pid
        String id = "test1";
        order.setId(id);
        order.setUid(uid);
        order.setPid(pid);
        order.setDetail(detail);
        order.setPrice(price);
        order.setOrderStatus(OrderStatusEnum.UNFINISH.getValue());
        order.setToken(null);

        if (_mapper.insert(order) > 0) {
            return id;
        } else {
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
}
