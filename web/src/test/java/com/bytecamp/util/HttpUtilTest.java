package com.bytecamp.util;

import com.bytecamp.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangke
 * @description: HttpUtilTest
 * @date 2019-08-10 14:37
 */
public class HttpUtilTest extends BaseTest {

    @Value("${seckill.token.url}")
    String url;

    @Test
    public void httpPostJson() throws Exception{

        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        CountDownLatch countDownLatch = new CountDownLatch(1000);

        for(int i=0;i<2;i++){
            executorService.execute(()->{
                try{
                    String res = HttpUtil.postJson(url, null);
                    System.out.println(res);
                }catch (Exception e){
                    System.out.println(e);
                }
                countDownLatch.countDown();

            });
        }

        countDownLatch.await();

        executorService.shutdown();

    }
}
