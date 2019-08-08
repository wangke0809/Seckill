package com.bytecamp.biz.dto;

import lombok.Data;

/**
 * @author wangke
 * @description: OrderDto
 * @date 2019-08-08 21:23
 */
@Data
public class OrderDto {
    /**
     * 订单号
     */
    String id;

    /**
     * 用户 id
     */
    int uid;

    /**
     * 商品i d
     */
    int pid;
}
