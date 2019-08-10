package com.bytecamp.web.vo;

import lombok.Data;

/**
 * @author wangke
 * @description: PayResultVO
 * @date 2019-08-08 23:07
 */
@Data
public class PayResultVO {

    /**
     * 状态枚举
     * 0：成功
     * 1:  失败
     */
    private int code;

    /**
     * 支付 token
     */
    private String token;
}
