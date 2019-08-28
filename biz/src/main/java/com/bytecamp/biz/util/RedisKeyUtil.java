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

    /**
     * 订单 状态
     */
    public static final String ORDER = "O:%s";

    /**
     * 用户订单列表
     */
    public static final String USERORDER = "R:%s";

    /**
     * ip 黑名单
     */
    public static final String IPBLACK = "I:%s";

    /**
     * user 黑名单
     */
    public static final String USERBLACK = "A:%s";

    /**
     * 记录用户登陆的 ip
     */
    public static final String USERIP = "B:%s";

    /**
     * 记录用户是否访问过 product 接口
     */
    public static final String REQUEST_PRODCUCT = "RP:%s";
}
