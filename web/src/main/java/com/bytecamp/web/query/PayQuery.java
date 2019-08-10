package com.bytecamp.web.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author wangke
 * @description: PayQuery
 * @date 2019-08-10 15:28
 */
@Data
public class PayQuery {

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
    @JsonProperty("order_id")
    private String orderId;
}
