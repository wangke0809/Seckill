package com.bytecamp.web.controller;

import com.bytecamp.biz.dto.OrderDto;
import com.bytecamp.biz.service.OrderService;
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
import javax.servlet.http.HttpServletResponse;
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
    public OrderResultVO order(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        OrderQuery orderQuery = JsonRequestUtil.getPostJson(request, OrderQuery.class);
        if (orderQuery == null) {
            log.error("order 请求参数为空");
            httpServletResponse.setStatus(403);
            return null;
        }
        try {
            OrderResultVO vo = new OrderResultVO();

            Long pidLong = orderQuery.getPid();
            if (pidLong < 133808073L || pidLong > 3163885158L) {
                log.error("pid 范围异常");
                httpServletResponse.setStatus(403);
                return null;
            }
            // 返回订单号
            String orderId = orderService.addOrder(orderQuery.getUid(), pidLong);

            if (orderId == null) {
                vo.setCode(OrderStatusEnum.FAILURE.getValue());
                return vo;
            } else {
                if (orderId.length() != 27) {
                    log.error("order 订单号不符合规则");
                    httpServletResponse.setStatus(403);
                    return null;
                }
                vo.setCode(OrderStatusEnum.SUCCESS.getValue());
                vo.setOrderId(orderId);
                return vo;
            }
        } catch (Exception e) {
            httpServletResponse.setStatus(403);
            log.error("[ order ] 异常 {}", orderQuery, e);
            return null;
        }
    }

    @ResponseBody
    @PostMapping(value = "/pay")
    public PayResultVO pay(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        PayQuery payQuery = JsonRequestUtil.getPostJson(request, PayQuery.class);
        if (payQuery == null) {
            log.error("pay 请求参数为空");
            httpServletResponse.setStatus(403);
            return null;
        }
        try {
            PayResultVO vo = new PayResultVO();

            String orderId = payQuery.getOrderId();
            if (orderId == null) {
                log.error("orderId 请求参数为空");
                httpServletResponse.setStatus(403);
                return null;
            }
            if (orderId.length() != 27) {
                log.error("orderId 不符合规则 {}", orderId);
                httpServletResponse.setStatus(403);
                return null;
            }
            Integer price = payQuery.getPrice();
            if (price == null || price < 1 || price > 100) {
                log.error("price 不符合规则");
                httpServletResponse.setStatus(403);
                return null;
            }
            String token = orderService.payOrder(orderId, payQuery.getUid(), price);

            if (StringUtils.isEmpty(token)) {
                vo.setCode(PayStatusEnum.FAILURE.getValue());
                httpServletResponse.setStatus(403);
                return null;
            } else {
                vo.setCode(PayStatusEnum.SUCCESS.getValue());
                vo.setToken(token);
            }
            return vo;
        } catch (Exception e) {
            log.error("[ pay ] 异常 {}", payQuery, e);
            httpServletResponse.setStatus(403);
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
