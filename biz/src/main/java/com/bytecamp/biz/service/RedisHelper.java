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
        String key = String.format(RedisKeyUtil.IPBLACK, ip);
        redisService.set(key, "0");
    }

    public Boolean ipIsBlack(String ip) {
        String key = String.format(RedisKeyUtil.IPBLACK, ip);
        if (redisService.exists(key)) {
            return true;
        }
        return false;
    }

    /**
     * 用户黑名单
     *
     * @param uid
     */
    public void userBlackAdd(Integer uid) {
        String key = String.format(RedisKeyUtil.USERBLACK, uid);
        redisService.set(key, "0");
    }

    public Boolean userIsBlack(Integer uid) {
        String key = String.format(RedisKeyUtil.USERBLACK, uid);
        if (redisService.exists(key)) {
            return true;
        } else {
            return false;
        }

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
            ipBlackAdd(ip);
            userBlackAdd(uid);
            return true;
        }
    }

    public void requestProductPathAdd(Integer uid) {
        String key = String.format(RedisKeyUtil.REQUEST_PRODCUCT, uid);
        redisService.set(key, "0");
    }

    public Boolean requestProductPathExists(Integer uid, String ip) {
        String key = String.format(RedisKeyUtil.REQUEST_PRODCUCT, uid);
        if (redisService.exists(key)) {
            return true;
        }
        userIsBlack(uid);
        if (ip != null) {
            ipBlackAdd(ip);
        }
        return false;
    }


}
