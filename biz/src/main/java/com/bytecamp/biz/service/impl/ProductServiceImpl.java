package com.bytecamp.biz.service.impl;

import com.bytecamp.biz.service.ProductService;
import com.bytecamp.dao.ProductMapper;
import com.bytecamp.model.Product;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: 具体实现
 * @date 2019-08-08 01:55
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Resource(name = "productMapper")
    private ProductMapper _mapper;

    /**
     * 商品表仅查询商品信息
     *
     * @param id 商品id
     * @return
     */
    @Override
    public Product selectByProductId(Integer id) {
        if (id == null || id < 0) {
            return null;
        }
        Product bean = _mapper.selectByPrimaryKey(id);
        return bean;
    }
}
