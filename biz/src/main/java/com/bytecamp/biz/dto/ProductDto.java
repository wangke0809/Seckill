package com.bytecamp.biz.dto;

import lombok.Data;

/**
 * @author wangke
 * @description: ProductDto
 * @date 2019-08-29 00:57
 */
@Data
public class ProductDto {
    private Long id;

    private Integer price;

    private String detail;

    private Integer count;
}
