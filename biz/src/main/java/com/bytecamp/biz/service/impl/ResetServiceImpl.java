package com.bytecamp.biz.service.impl;

import com.bytecamp.biz.service.OrderService;
import com.bytecamp.biz.service.RedisService;
import com.bytecamp.biz.service.ResetService;
import com.bytecamp.biz.util.RedisKeyUtil;
import com.bytecamp.dao.OrderMapper;
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

    @Resource
    OrderService orderService;

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

            orderService.stockMapClear();

            // 库存 keys
            Set<String> sKeys = redisService.getAllKeys("S:*");

            // uid 购买的 pid keys
            Set<String> uKeys = redisService.getAllKeys("U:*");

            for (String key : sKeys) {
                redisService.del(key);
            }

            for (String key : uKeys) {
                redisService.del(key);
            }

            Set<String> uidKeys = redisService.getAllKeys("R:*");

            for (String uid : uidKeys) {
                String userOrders = String.format(RedisKeyUtil.USERORDER, uid.substring(2, uid.length()));
                List<String> orderOds = redisService.lrange(userOrders, 0, -1);
                for (String orderId : orderOds) {
                    String orderKey = String.format(RedisKeyUtil.ORDER, orderId);
                    redisService.del(orderKey);
                }
                redisService.del(uid);
            }

            log.info("[ reset] reset 成功");

            return true;

        } catch (Exception e) {
            log.error("[ reset] reset 异常", e);
            return false;
        }
    }
}
