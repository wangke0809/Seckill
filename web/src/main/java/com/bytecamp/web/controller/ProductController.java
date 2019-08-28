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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
     *
     * @param pid 商品 id
     * @return 商品信息
     */
    @ResponseBody
    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public ProductVO product(String pid, HttpServletResponse httpServletResponse) {
        Long pidLong = null;
        try {
            pidLong = Long.valueOf(pid);
        } catch (Exception e) {
            log.error("pid 解析失败", e);
            httpServletResponse.setStatus(403);
            return null;
        }
        if (pidLong < 133808073L || pidLong > 3163885158L) {
            log.error("pid 范围异常");
            httpServletResponse.setStatus(403);
            return null;
        }
        Product product = productService.getProductById(pidLong);
        if (product != null) {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(product, vo);
            vo.setPid(product.getId());
            return vo;
        } else {
            log.error("pid: {} 商品不存在", pid);
        }
        httpServletResponse.setStatus(403);
        return null;
    }
}
