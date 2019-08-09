package com.bytecamp.service;

import com.bytecamp.BaseTest;
import com.bytecamp.biz.service.SessionService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: SessionServiceTest
 * @date 2019-08-09 18:28
 */
public class SessionServiceTest extends BaseTest {

    @Resource
    SessionService sessionService;

    @Test
    public void getAll(){
        System.out.println(sessionService.getAll());
    }
}
