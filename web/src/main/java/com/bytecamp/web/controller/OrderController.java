package com.bytecamp.web.controller;

import com.bytecamp.biz.service.OrderService;
import com.bytecamp.web.enums.OrderStatus;
import com.bytecamp.web.query.OrderQuery;
import com.bytecamp.web.util.JsonRequestUtil;
import com.bytecamp.web.vo.OrderResultVO;
import com.bytecamp.web.vo.PayResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wangke
 * @description: OrderController
 * @date 2019-08-08 22:54
 */
@Controller
@Slf4j
public class OrderController {

    @Resource
    OrderService orderService;

    @ResponseBody
    @PostMapping(value = "/order")
    public OrderResultVO order(HttpServletRequest request){
        OrderQuery orderQuery = JsonRequestUtil.getPostJson(request, OrderQuery.class);
        if(orderQuery == null){
            log.error("order 请求参数为空");
            return null;
        }
        try {
            OrderResultVO vo = new OrderResultVO();
            // 返回订单号
            String orderId = orderService.addOrder(orderQuery.getUid(), orderQuery.getPid());
            if(orderId == null){
                vo.setCode(OrderStatus.FAILURE.getValue());
                return vo;
            }else{
                vo.setCode(OrderStatus.SUCCESS.getValue());
                vo.setOrderId(orderId);
                return vo;
            }
        }catch (Exception e){
            log.error("order 异常", e);
            return null;
        }
    }

    @ResponseBody
    @PostMapping(value = "/pay")
    public PayResultVO pay(){
        PayResultVO vo = new PayResultVO();
        vo.setCode(0);
        return vo;
    }

}
