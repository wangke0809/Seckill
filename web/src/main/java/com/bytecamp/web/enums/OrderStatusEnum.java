package com.bytecamp.web.enums;

/**
 * @author wangke
 * @description: TOOrderStatusDO
 * @date 2019-08-10 01:39
 */
public enum OrderStatusEnum {

    /**
     * 下单成功
     */
    SUCCESS(0),
    /**
     * 下单失败
     */
    FAILURE(1);

    private Integer value;

    OrderStatusEnum(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
}
