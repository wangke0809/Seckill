package com.bytecamp.biz.util;

import org.springframework.util.StringUtils;

/**
 * @author wangke
 * @description: RedisKeyUtil
 * @date 2019-08-09 12:51
 */
public class RedisKeyUtil {

    /**
     * 根据商品 id 缓存商品的键
     */
    public static final String PRODUCT = "P:%s";

    /**
     * 根据商品 id 缓存商品库存的键
     */
    public static final String STOCK = "S:%s";

    /**
     * session 缓存健
     */
    public static final String SESSION = "E";

    /**
     * uid 购买的 pid
     */
    public static final String USERPRODUCT = "U:%s:%s";
}
