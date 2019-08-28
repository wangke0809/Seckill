package com.bytecamp.biz.service.impl;

import com.bytecamp.biz.service.OrderService;
import com.bytecamp.biz.service.RedisDB1Service;
import com.bytecamp.biz.service.RedisService;
import com.bytecamp.biz.service.ResetService;
import com.bytecamp.biz.util.RedisKeyUtil;
import com.bytecamp.dao.OrderMapper;
import com.bytecamp.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author wangke
 * @description: ResetServiceImpl
 * @date 2019-08-10 16:18
 */
@Service
@Slf4j
public class ResetServiceImpl implements ResetService {

    @Value("${seckill.hosts}")
    private String hosts;

    @Resource
    OrderService orderService;

    @Resource
    RedisDB1Service db1Service;

    @Value("${seckill.reset.token}")
    String token;

    /**
     * reset
     *
     * @param token
     */
    @Override
    public Boolean reset(String token) {

        if (StringUtils.isEmpty(token) || !token.equals(this.token)) {
            log.error("[ reset ] reset token 不正确");
            return false;
        }

        try {

            // TODO: 键用常量获取 format(KEY, "*")
            orderService.stockMapClear();

            Long start = System.currentTimeMillis();

            db1Service.flushdb();

            String[] host = hosts.split(",");

            try {
                for (String s : host) {
                    log.info("reset others http://" + s + "/reset22222222223");
                    String res = HttpUtil.get("http://" + s + "/reset22222222223");
                    log.info(" reset22222222223 {} {}", s, res);
                }
            } catch (Exception e) {

            }


            log.info("[ reset] reset 成功 time: {}", (System.currentTimeMillis() - start) / 1000.0);

            return true;

        } catch (Exception e) {
            log.error("[ reset] reset 异常", e);
            return false;
        }
    }
}
