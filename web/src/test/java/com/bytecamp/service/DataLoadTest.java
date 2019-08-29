package com.bytecamp.service;

import com.alibaba.fastjson.JSON;
import com.bytecamp.BaseTest;
import com.bytecamp.biz.dto.ProductDto;
import com.bytecamp.biz.service.RedisService;
import com.bytecamp.biz.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * @author wangke
 * @description: TODO
 * @date 2019-08-29 09:27
 */
@Slf4j
public class DataLoadTest extends BaseTest {

    @Resource
    RedisService redisService;

    @Test
    public void test() throws Exception {
        String path = "/Users/pizi/111.txt";
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
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
            log.info("i " + i);
            i++;
            if (i == 2000000) {
                break;
            }
        }
    }
}
