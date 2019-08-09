package com.bytecamp.service;

import com.bytecamp.BaseTest;
import com.bytecamp.biz.service.ProductService;
import com.bytecamp.model.Product;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: ProductServiceTest
 * @date 2019-08-08 02:48
 */
public class ProductServiceTest extends BaseTest {

    @Resource
    ProductService productService;

    @Test
    public void selectById() {
        for (int i = 0; i < 5; i++) {
            Product product = productService.getProductById(1);
            System.out.println(product);
        }

    }
}
