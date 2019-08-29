package com.bytecamp.web.schedul;

import com.alibaba.fastjson.JSON;
import com.bytecamp.biz.dto.ProductDto;
import com.bytecamp.biz.service.RedisService;
import com.bytecamp.biz.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * @author wangke
 * @description: TODO
 * @date 2019-08-29 04:21
 */
@Slf4j
@Component
public class DataLoadSchedul {

    @Value("${seckill.machine-id}")
    private int machineId;

    @Resource
    RedisService redisService;

    @Scheduled(fixedDelay = 5000)
    public void task() throws Exception {
        if (machineId != 3) {
            return;
        }
        String need = redisService.get("loaddata");
        if (need == null) {

        } else {
            return;
        }
        try {
            String path = "/var/lib/mysql-files/student.txt";
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            String str;
            int i = 0;
            while ((str = bf.readLine()) != null) {
                String[] d = str.split("\t");
                ProductDto dto = new ProductDto();
                dto.setId(Long.valueOf(d[0]));
                dto.setDetail(d[2]);
                dto.setCount(Integer.valueOf(d[3]));
                dto.setPrice(Integer.valueOf(d[1]));
                String key = String.format(RedisKeyUtil.PRODUCT, dto.getId());
                redisService.setex(key, 360000, JSON.toJSONString(dto));
                if (i % 10000 == 0) {
                    log.info("data load {}", i);
                }
                i++;
                if (i == 4000000) {
                    break;
                }
            }
            bf.close();
            redisService.set("loaddata", "1");
        } catch (Exception e) {

        }


    }

    public static void main(String[] args) throws Exception {
        String path = "/Users/pizi/111.txt";
        FileReader fr = new FileReader(path);
        BufferedReader bf = new BufferedReader(fr);
        String str;
        int i = 0;
        while ((str = bf.readLine()) != null) {
            String[] d = str.split("\t");

            i++;
            if (i == 2000000) {
                break;
            }
        }
    }

}
