package com.bytecamp.web.controller;

import com.bytecamp.biz.service.RedisService;
import com.bytecamp.dao.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangke
 * @description: 测试控制器
 * @date 2019-07-27 02:11
 */
@Controller
@Slf4j
public class TestController {

    @Resource(name = "productMapper")
    private ProductMapper _mapper;

    @Resource
    RedisService redisService;

    @ResponseBody
    @RequestMapping("/test")
    public String test(Integer num) throws Exception {
//        redisService.set("mysql", num.toString());
        Integer n = Integer.valueOf(num);

//        redisService.del("mysql");
        ExecutorService service = Executors.newFixedThreadPool(n);
        CountDownLatch countDownLatch = new CountDownLatch(n);
        log.info("start " + n);
        Long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            service.submit(() -> {
                Random random = new Random();
                Long pid = Long.valueOf(random.nextInt(303007708) + 933808073);
                _mapper.selectByPrimaryKey(pid);
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        log.info("end " + (System.currentTimeMillis() - start) / 1000.0 + " s");

        service.shutdown();

        return "end " + (System.currentTimeMillis() - start) / 1000.0 + " s";
    }
}
