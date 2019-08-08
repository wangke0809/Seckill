package com.bytecamp.web.controller;

import com.bytecamp.web.vo.OrderResultVO;
import com.bytecamp.web.vo.PayResultVO;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wangke
 * @description: OrderController
 * @date 2019-08-08 22:54
 */
@Controller
public class OrderController {

    @ResponseBody
    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public OrderResultVO order(){
        OrderResultVO vo = new OrderResultVO();
        vo.setCode(0);
        vo.setOrderId("asd");
        return vo;
    }

    @ResponseBody
    @RequestMapping(value = "/pay", method = RequestMethod.GET)
    public PayResultVO pay(){
        PayResultVO vo = new PayResultVO();
        vo.setCode(0);
        return vo;
    }


}
