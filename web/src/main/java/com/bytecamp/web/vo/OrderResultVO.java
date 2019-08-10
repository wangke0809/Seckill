package com.bytecamp.web.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author wangke
 * @description: OrderResultVO
 * @date 2019-08-08 22:55
 */
@Data
public class OrderResultVO {

    /**
     * 状态枚举
     * 0：成功
     * 1:  没有剩余
     */
    private int code;

    /**
     * 订单编号
     */
    @JsonProperty("order_id")
    private String orderId;
}
