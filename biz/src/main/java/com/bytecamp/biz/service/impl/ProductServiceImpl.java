package com.bytecamp.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.bytecamp.biz.dto.ProductDto;
import com.bytecamp.biz.service.ProductService;
import com.bytecamp.biz.service.RedisService;
import com.bytecamp.biz.util.RedisKeyUtil;
import com.bytecamp.dao.ProductMapper;
import com.bytecamp.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;

/**
 * @author wangke
 * @description: 具体实现
 * @date 2019-08-08 01:55
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Resource(name = "productMapper")
    private ProductMapper _mapper;

    @Resource
    RedisService redisService;

    @Value("${seckill.product.cache-time}")
    private int productCacheTime;

    /**
     * 商品表仅查询商品信息
     *
     * @param id 商品id
     * @return
     */
    @Override
    public Product getProductById(Long id) {
        if (id == null || id < 0) {
            return null;
        }
        // 首先从缓存中查询
        String key = String.format(RedisKeyUtil.PRODUCT, id);
        String product = redisService.get(key);
        Product bean = null;
        // 命中缓存
        if (product != null) {
            try {
                bean = JSON.parseObject(product, Product.class);
                log.info("商品 {} 命中缓存", id);
                return bean;
            } catch (Exception e) {
                log.error("反序列化商品信息失败", e);
            }
        }
        // 缓存不存在
        if (bean == null) {
            bean = _mapper.selectByPrimaryKey(id);
            if (bean == null) {
                return null;
            }
            ProductDto dto = new ProductDto();
            BeanUtils.copyProperties(bean, dto);
            // TODO：缓存多久合适
            redisService.setex(key, productCacheTime, JSON.toJSONString(dto));
        }

        return bean;
    }
}
