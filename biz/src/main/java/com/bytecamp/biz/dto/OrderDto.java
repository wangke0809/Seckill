package com.bytecamp.biz.dto;

import lombok.Data;

/**
 * @author wangke
 * @description: OrderDto
 * @date 2019-08-08 21:23
 */
@Data
public class OrderDto {
    private String id;

    private Integer uid;

    private Long pid;

    private String detail;

    private Integer price;

    private Byte orderStatus;

    private String token;
}
