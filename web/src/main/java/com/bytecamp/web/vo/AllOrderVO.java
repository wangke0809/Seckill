package com.bytecamp.web.vo;

import lombok.Data;

import java.util.List;

/**
 * @author wangke
 * @description: AllOrderVO
 * @date 2019-08-10 21:09
 */
@Data
public class AllOrderVO {

    /**
     * data
     */
    private List<OrderVO> data;
}
