package com.bytecamp.biz.service;

import com.alibaba.fastjson.JSON;
import com.bytecamp.biz.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * @author wangke
 * @description: MQService
 * @date 2019-08-08 21:13
 */
@Service
@Slf4j
public class MQService {

    @Resource
    JmsTemplate jmsTemplate;

    Destination destination = new ActiveMQQueue("order");

    /**
     * 发送点对点的订单队列消息
     * @param orderDto
     */
    public void sendMessageToQueue(final OrderDto orderDto) {
        String message = JSON.toJSONString(orderDto);
        jmsTemplate.convertAndSend(destination, message);
    }

    /**
     * 接受点对点队列消息
     * @param message
     */
    @JmsListener(destination = "order", containerFactory = "queueListenerFactory")
    public void receiveOrderMessage(Message message){
        TextMessage textMessage = (TextMessage) message;
        try{
            String str = textMessage.getText();
            OrderDto orderDto = JSON.parseObject(str, OrderDto.class);
            System.out.println(orderDto);
        }catch (Exception e){
            log.error("消息队列订阅异常", e);
        }

    }
}
