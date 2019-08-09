package com.bytecamp.service;

import com.bytecamp.BaseTest;
import com.bytecamp.biz.service.RedisService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangke
 * @description: RedisServiceTest
 * @date 2019-08-08 14:03
 */
public class RedisServiceTest extends BaseTest {

    @Resource
    RedisService redisService;

    @Test
    public void test() {
        redisService.set("key", "value");
        String v = redisService.get("key");
        System.out.println(v);
    }

    /**
     * 测试库存减少
     */
    @Test
    public void testDecr() throws Exception{
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        AtomicInteger res = new AtomicInteger(0);
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        redisService.set("num", "100");

        for (int i = 0; i < 1000; i++) {
            executorService.execute(() -> {
                Long l = redisService.decr("num");
                if( l >= 0){
                    res.incrementAndGet();
                }
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        String l = redisService.get("num");

        System.out.println(l);
        System.out.println(res);
    }

    @Test
    public void testDecrNull(){
        // 不存在的键值自减直接返回 -1
        System.out.println(redisService.decr("aaa"));
    }

    @Test
    public void keys(){
        System.out.println(redisService.getAllKeys("s*"));
    }
}
