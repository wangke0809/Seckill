package com.bytecamp.biz.util;

/**
 * @author wangke
 * @description: RedisKeyUtil
 * @date 2019-08-09 12:51
 */
public class RedisKeyUtil {

    /**
     * 根据商品 id 缓存商品的键
     */
    public static final String PRODUCT = "P_%s";

    /**
     * 根据商品 id 缓存商品库存的键
     */
    public static final String STOCK = "S_%s";

}
