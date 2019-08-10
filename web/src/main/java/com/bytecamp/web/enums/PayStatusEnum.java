package com.bytecamp.web.enums;

/**
 * @author wangke
 * @description: PayStatusEnum
 * @date 2019-08-10 15:29
 */
public enum PayStatusEnum {

    /**
     * 下单成功
     */
    SUCCESS(0),
    /**
     * 下单失败
     */
    FAILURE(1);

    private Integer value;

    PayStatusEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
