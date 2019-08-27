package com.bytecamp.service;

import com.bytecamp.BaseTest;
import com.bytecamp.biz.service.RedisLuaHelper;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangke
 * @description: RedisLuaHelperTest
 * @date 2019-08-27 12:47
 */
public class RedisLuaHelperTest extends BaseTest {

    @Resource
    RedisLuaHelper redisLuaHelper;

    @Test
    public void test() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        final CountDownLatch countDownLatch = new CountDownLatch(1000);
        redisLuaHelper.stockIncr("test2");
        System.out.println("start");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                redisLuaHelper.stockIncr("test2");
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("Time Used: " + (System.currentTimeMillis() - startTime) / 1000.0);
    }
}
