package com.bytecamp.web.controller;

import com.bytecamp.biz.service.ProductService;
import com.bytecamp.model.Product;
import com.bytecamp.web.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: ProductController
 * @date 2019-08-08 22:31
 */
@Controller
@Slf4j
public class ProductController {

    @Resource
    ProductService productService;

    /**
     * 获取商品信息
     * @param pid 商品 id
     * @return 商品信息
     */
    @ResponseBody
    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public ProductVO product(Long pid) {
        Product product = productService.getProductById(pid);
        if (product != null) {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(product, vo);
            vo.setPid(product.getId());
            return vo;
        }else{
            log.error("pid: {} 商品不存在", pid);
        }
        return null;
    }
}
