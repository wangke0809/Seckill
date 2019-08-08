package com.bytecamp.service;

import com.bytecamp.BaseTest;
import com.bytecamp.biz.service.OrderService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: OrderServiceTest
 * @date 2019-08-08 02:52
 */
public class OrderServiceTest extends BaseTest {

    @Resource
    OrderService orderService;

    @Test
    public void addOrder() {
        String id = orderService.addOrder(1, 2, "aaa啊啊啊", 20);
        System.out.println(id);
    }

    @Test
    public void getAll() {
        System.out.println(orderService.getAllOrders());
    }

    @Test
    public void payOrder() {
        orderService.payOrder("test");
    }

    @Test
    public void delAll(){
        orderService.delAllOrders();
    }
}
