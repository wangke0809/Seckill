package com.bytecamp.web.controller;

import com.bytecamp.biz.dto.OrderDto;
import com.bytecamp.biz.service.OrderService;
import com.bytecamp.model.Order;
import com.bytecamp.web.enums.OrderStatusEnum;
import com.bytecamp.web.enums.PayStatusEnum;
import com.bytecamp.web.query.OrderQuery;
import com.bytecamp.web.query.PayQuery;
import com.bytecamp.web.util.JsonRequestUtil;
import com.bytecamp.web.vo.AllOrderVO;
import com.bytecamp.web.vo.OrderResultVO;
import com.bytecamp.web.vo.OrderVO;
import com.bytecamp.web.vo.PayResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
    public OrderResultVO order(HttpServletRequest request) {
        OrderQuery orderQuery = JsonRequestUtil.getPostJson(request, OrderQuery.class);
        if (orderQuery == null) {
            log.error("order 请求参数为空");
            return null;
        }
        try {
            OrderResultVO vo = new OrderResultVO();
            // 返回订单号
            String orderId = orderService.addOrder(orderQuery.getUid(), orderQuery.getPid());
            if (orderId == null) {
                vo.setCode(OrderStatusEnum.FAILURE.getValue());
                return vo;
            } else {
                vo.setCode(OrderStatusEnum.SUCCESS.getValue());
                vo.setOrderId(orderId);
                return vo;
            }
        } catch (Exception e) {
            log.error("[ order ] 异常 {}", orderQuery, e);
            return null;
        }
    }

    @ResponseBody
    @PostMapping(value = "/pay")
    public PayResultVO pay(HttpServletRequest request) {
        PayQuery payQuery = JsonRequestUtil.getPostJson(request, PayQuery.class);
        if (payQuery == null) {
            log.error("pay 请求参数为空");
            return null;
        }
        try {
            PayResultVO vo = new PayResultVO();

            String token = orderService.payOrder(payQuery.getOrderId(), payQuery.getUid(), payQuery.getPrice());

            if (StringUtils.isEmpty(token)) {
                vo.setCode(PayStatusEnum.FAILURE.getValue());
            } else {
                vo.setCode(PayStatusEnum.SUCCESS.getValue());
                vo.setToken(token);
            }
            return vo;
        } catch (Exception e) {
            log.error("[ pay ] 异常 {}", payQuery, e);
            return null;
        }
    }

    @ResponseBody
    @GetMapping("/result")
    public AllOrderVO result(Integer uid) {
        AllOrderVO allOrderVO = new AllOrderVO();
        List<OrderVO> data = new ArrayList<>();
        allOrderVO.setData(data);

        try {
            List<OrderDto> orders = orderService.getAllOrders(uid);
            for (OrderDto order : orders) {
                OrderVO vo = new OrderVO();
                BeanUtils.copyProperties(order, vo);
                vo.setOrderId(order.getId());
                vo.setStatus(order.getOrderStatus().intValue());
                data.add(vo);
            }
        } catch (Exception e) {
            log.error("result 异常", e);
        }
        return allOrderVO;
    }
}
