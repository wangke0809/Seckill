package com.bytecamp.biz.service.impl;

import com.bytecamp.biz.service.RedisService;
import com.bytecamp.biz.service.ResetService;
import com.bytecamp.dao.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author wangke
 * @description: ResetServiceImpl
 * @date 2019-08-10 16:18
 */
@Service
@Slf4j
public class ResetServiceImpl implements ResetService {

    @Resource(name = "orderMapper")
    private OrderMapper _mapper;

    @Resource
    RedisService redisService;

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
            // 清除订单数据库，truncate 速度最快
            _mapper.truncate();

            // 清除除了 session 以外的 redis 缓存

            // 库存 keys
            Set<String> sKeys = redisService.getAllKeys("S:*");

            // uid 购买的 pid keys
            Set<String> uKeys = redisService.getAllKeys("U:*");

            // TODO: 商品缓存不用清除应该也可以

            for (String key : sKeys) {
                redisService.del(key);
            }

            for (String key : uKeys) {
                redisService.del(key);
            }

            log.info("[ reset] reset 成功");

            return true;

        } catch (Exception e) {
            log.error("[ reset] reset 异常", e);
            return false;
        }
    }
}
