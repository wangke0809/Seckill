package com.bytecamp.web.schedul;

import com.bytecamp.biz.service.ProductService;
import com.bytecamp.biz.service.RedisService;
import com.bytecamp.dao.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangke
 * @description: TODO
 * @date 2019-08-28 16:51
 */
@Slf4j
//@Component
public class MysqlTestSchedul {

    @Resource
    RedisService redisService;

    @Resource(name = "productMapper")
    private ProductMapper _mapper;

    @Scheduled(fixedDelay = 1000)
    public void task() throws Exception {
        String nn = redisService.get("mysql");

        if (nn == null) {
            return;
        }

        Integer n = Integer.valueOf(nn);

        redisService.del("mysql");
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


    }
}
