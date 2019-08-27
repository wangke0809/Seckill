package com.bytecamp.biz.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: RedisLuaHelper
 * @date 2019-08-27 11:28
 */
@Service
public class RedisLuaHelper {

    @Resource
    private JedisPool jedisPool;

    private String script = "if redis.call('exists', KEYS[1]) > 0 then\n"
            + "if tonumber(redis.call('get', KEYS[1])) <= 0 then\n"
            + "redis.call('set', KEYS[1], 1)\n"
            + "else\n"
            + "redis.call('incr', KEYS[1])\n"
            + "end\n"
            + "end";

    // lua 脚本 hash
    private volatile String sha3 = null;

    /**
     * 库存加 1
     *
     * @param key
     */
    public void stockIncr(String key) {
        if (sha3 == null) {
            synchronized (RedisLuaHelper.class) {
                if (sha3 == null) {
                    sha3 = jedisPool.getResource().scriptLoad(script);
                }
            }
        }
        jedisPool.getResource().evalsha(sha3, 1, key);
    }
}
