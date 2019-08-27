package com.bytecamp.web.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author wangke
 * @description: OrderVO
 * @date 2019-08-10 21:44
 */
@Data
public class OrderVO {

    private Integer uid;

    private Long pid;

    private String detail;

    @JsonProperty("order_id")
    private String orderId;

    private Integer price;

    private Integer status;

    private String token;
}
