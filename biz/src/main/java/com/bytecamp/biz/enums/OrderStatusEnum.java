package com.bytecamp.biz.enums;

/**
 * @author wangke
 * @description: 订单状态
 * @date 2019-08-08 02:22
 */
public enum OrderStatusEnum {
    /**
     * 未支付
     */
    UNFINISH("未支付", (byte) 0),

    /**
     * 已支付
     */
    FINISH("已支付", (byte) 1);

    private String desc;
    private byte value;

    OrderStatusEnum(String desc, byte value) {
        this.desc = desc;
        this.value = value;
    }

    public Byte getValue() {
        return value;
    }

}