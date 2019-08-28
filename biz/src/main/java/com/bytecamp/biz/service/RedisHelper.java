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
    RedisDB1Service redisDB1Service;

    /**
     * ip 黑名单
     *
     * @param ip
     */
    public void ipBlackAdd(String ip) {
        String key = String.format(RedisKeyUtil.IPBLACK, ip);
        redisDB1Service.set(key, "0");
    }

    public Boolean ipIsBlack(String ip) {
        String key = String.format(RedisKeyUtil.IPBLACK, ip);
        if (redisDB1Service.exists(key)) {
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
        redisDB1Service.set(key, "0");
    }

    public Boolean userIsBlack(Integer uid) {
        String key = String.format(RedisKeyUtil.USERBLACK, uid);
        if (redisDB1Service.exists(key)) {
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
        if (ip == null) {
            return;
        }
        redisDB1Service.set(key, ip);
    }

    public Boolean userIpIsChange(Integer uid, String ip) {
        if (ip == null) {
            return false;
        }
        String key = String.format(RedisKeyUtil.USERIP, uid);
        String uip = redisDB1Service.get(key);
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
        redisDB1Service.set(key, "0");
    }

    public Boolean requestProductPathExists(Integer uid, String ip) {
        String key = String.format(RedisKeyUtil.REQUEST_PRODCUCT, uid);
        if (redisDB1Service.exists(key)) {
            return true;
        }
        userIsBlack(uid);
        if (ip != null) {
            ipBlackAdd(ip);
        }
        return false;
    }


}
