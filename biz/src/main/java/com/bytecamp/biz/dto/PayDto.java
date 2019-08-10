package com.bytecamp.biz.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author wangke
 * @description: PayDto
 * @date 2019-08-10 14:48
 */
@Data
public class PayDto {

    /**
     * uid
     */
    private Integer uid;

    /**
     * price
     */
    private Integer price;

    /**
     * orderId
     */
    @JSONField(name = "order_id")
    private String orderId;
}
