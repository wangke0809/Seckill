package com.bytecamp.web.enums;

/**
 * @author wangke
 * @description: ResetStatusEnum
 * @date 2019-08-10 21:00
 */
public enum  ResetStatusEnum {
    /**
     * 下单成功
     */
    SUCCESS(0),
    /**
     * 下单失败
     */
    FAILURE(1);

    private Integer value;

    ResetStatusEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
