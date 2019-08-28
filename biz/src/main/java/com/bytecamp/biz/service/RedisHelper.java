package com.bytecamp.biz.service;

import com.bytecamp.biz.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: RedisHelper
 * @date 2019-08-28 10:18
 */
@Slf4j
@Service
public class RedisHelper {

    @Resource
    RedisService redisService;

    /**
     * ip 黑名单
     *
     * @param ip
     */
    public void ipBlackAdd(String ip) {
        String key = String.format(RedisKeyUtil.IPBLCAK, ip);
        redisService.set(key, "0");
    }

    public Boolean ipIsBlack(String ip) {
        String key = String.format(RedisKeyUtil.IPBLCAK, ip);
        if (redisService.exists(key)) {
            return true;
        }
        return false;
    }

    /**
     * 用户 uid 对应的 ip
     *
     * @param uid
     * @param ip
     */
    public void userIpAdd(Integer uid, String ip) {
        String key = String.format(RedisKeyUtil.USERIP, uid);
        redisService.set(key, ip);
    }

    public Boolean userIpIsChange(Integer uid, String ip) {
        String key = String.format(RedisKeyUtil.USERIP, uid);
        String uip = redisService.get(key);
        // 第一次访问
        if (uip == null) {
            userIpAdd(uid, ip);
            return false;
        }

        if (uip.equals(ip)) {
            return false;
        } else {
            return true;
        }
    }


}
