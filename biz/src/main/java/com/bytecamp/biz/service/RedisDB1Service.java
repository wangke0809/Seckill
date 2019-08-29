package com.bytecamp.biz.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangke
 * @description: RedisDB1Service
 * @date 2019-08-28 23:07
 */
@Service
@Slf4j
public class RedisDB1Service {

    @Resource
    private JedisPool jedisPool;

    public String set(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return null;
        }
        jedis.select(1);
        String res = null;
        try {
            res = jedis.set(key, value);
        } catch (Exception e) {
            log.error("redis error", e);
        } finally {
            jedis.close();
        }
        return res;
    }

    public Boolean exists(String key) {
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return null;
        }
        jedis.select(1);
        Boolean res = null;
        try {
            res = jedis.exists(key);
        } catch (Exception e) {
            log.error("redis error", e);
        } finally {
            jedis.close();
        }
        return res;
    }

    public String set(String key, String value, String nxxx) {
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return null;
        }
        jedis.select(1);
        String res = null;
        try {
            res = jedis.set(key, value, nxxx);
        } catch (Exception e) {
            log.error("redis error", e);
        } finally {
            jedis.close();
        }
        return res;
    }

    public List<String> lrange(String key, long start, long end) {
        List<String> result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.lrange(key, start, end);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return null;
        }
        jedis.select(1);
        String res = null;
        try {
            res = jedis.get(key);
        } catch (Exception e) {
            log.error("redis error", e);
        } finally {
            jedis.close();
        }
        return res;
    }

    public Long del(String key) {
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return null;
        }
        jedis.select(1);
        Long res = null;
        try {
            res = jedis.del(key);
        } catch (Exception e) {
            log.error("redis error", e);
        } finally {
            jedis.close();
        }
        return res;
    }

    public void flushdb() {
        Jedis jedis = jedisPool.getResource();
        jedis.select(1);
        jedis.flushDB();
    }

    public Long decr(String key) {
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return null;
        }
        jedis.select(1);
        Long res = null;
        try {
            res = jedis.decr(key);
        } catch (Exception e) {
            log.error("redis error", e);
        } finally {
            jedis.close();
        }
        return res;
    }

    public Long lpush(String key, String string) {
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return null;
        }
        jedis.select(1);
        Long res = null;
        try {
            res = jedis.lpush(key, string);
        } catch (Exception e) {
            log.error("redis error", e);
        } finally {
            jedis.close();
        }
        return res;
    }
}
