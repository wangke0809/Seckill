package com.bytecamp.biz.service;

import com.bytecamp.model.Product;

/**
 * @author wangke
 * @description: 商品表服务
 * @date 2019-08-08 01:54
 */
public interface ProductService {
    /**
     * 商品表仅查询商品信息
     * @param id 商品id
     * @return
     */
    Product getProductById(Long id);
}
