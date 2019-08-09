package com.bytecamp.web.enums;

/**
 * @author wangke
 * @description: TOOrderStatusDO
 * @date 2019-08-10 01:39
 */
public enum OrderStatus {

    /**
     * 下单成功
     */
    SUCCESS(0),
    /**
     * 下单失败
     */
    FAILURE(1);

    private Integer value;

    OrderStatus(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
}
