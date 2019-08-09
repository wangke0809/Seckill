package com.bytecamp.web.query;

import lombok.Data;

/**
 * @author wangke
 * @description: OrderQuery
 * @date 2019-08-09 22:53
 */
@Data
public class OrderQuery {

    /**
     * user id
     */
    private Integer uid;

    /**
     * product id
     */
    private Integer pid;
}
