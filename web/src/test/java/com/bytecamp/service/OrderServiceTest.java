package com.bytecamp.service;

import com.bytecamp.BaseTest;
import com.bytecamp.biz.service.OrderService;
import com.bytecamp.dao.OrderMapper;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangke
 * @description: OrderServiceTest
 * @date 2019-08-08 02:52
 */
public class OrderServiceTest extends BaseTest {

    @Resource
    OrderService orderService;

    @Resource(name = "orderMapper")
    private OrderMapper _mapper;

    @Test
    public void addOrder() {
        Long id = orderService.addOrder(1, 2L);
        System.out.println(id);
    }

    @Test
    public void getAll() {
        System.out.println(orderService.getAllOrders());
    }

    @Test
    public void payOrder() {
        orderService.payOrder(1L, 1, 1);
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
        CountDownLatch countDownLatch = new CountDownLatch(200);
        AtomicInteger res = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(200);

        for (int i = 0; i < 200; i++) {
            executorService.execute(() -> {
                try{
                    orderService.addOrder(175230, 176472613L);
                    res.incrementAndGet();
                }catch (Exception e){
                    System.out.println("eeeeeeeee!");
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        }catch (Exception e){
            System.out.println("Exception!! " + e);
        }
        System.out.println("res " + res);
        executorService.shutdown();
    }

    @Test
    public void truncate(){
        _mapper.truncate();
    }
}
