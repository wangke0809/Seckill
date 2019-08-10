package com.bytecamp.service;

import com.bytecamp.BaseTest;
import com.bytecamp.biz.dto.OrderDto;
import com.bytecamp.biz.service.MQService;
import com.bytecamp.biz.service.ProductService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: MQServiceTest
 * @date 2019-08-08 21:39
 */
public class MQServiceTest extends BaseTest {

    @Resource
    MQService mqService;

    @Resource
    ProductService productService;

    @Test
    public void sendMsg(){
        OrderDto orderDto = new OrderDto();
        orderDto.setId("asdasd");

        orderDto.setProduct(productService.getProductById(176467513));
        mqService.sendMessageToQueue(orderDto);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
