package com.bytecamp.service;

import com.bytecamp.BaseTest;
import com.bytecamp.biz.service.OrderService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        String id = orderService.addOrder(1, 2);
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
    public void delAll() {
        orderService.delAllOrders();
    }

    @Test
    public void orderExists() {
        System.out.println(orderService.orderExists(1, 1));
    }

    /**
     * 测试并发下单
     */
    @Test
    public void addManyManyManyOrder() {
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        for (int i = 0; i < 200; i++) {
            executorService.execute(() -> {
                orderService.addOrder(175230, 176472613);
            });
        }
    }
}
