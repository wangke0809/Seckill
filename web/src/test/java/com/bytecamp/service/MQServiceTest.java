package com.bytecamp.service;

import com.bytecamp.BaseTest;
import com.bytecamp.biz.dto.OrderDto;
import com.bytecamp.biz.service.MQService;
import org.aspectj.weaver.ast.Or;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: TODO
 * @date 2019-08-08 21:39
 */
public class MQServiceTest extends BaseTest {

    @Resource
    MQService mqService;

    @Test
    public void sendMsg(){
        OrderDto orderDto = new OrderDto();
        orderDto.setId("asdasd");
        orderDto.setPid(1);
        orderDto.setPid(2);
        mqService.sendMessageToQueue(orderDto);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
