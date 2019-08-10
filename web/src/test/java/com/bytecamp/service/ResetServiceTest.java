package com.bytecamp.service;

import com.bytecamp.BaseTest;
import com.bytecamp.biz.service.ResetService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: ResetServiceTest
 * @date 2019-08-10 16:28
 */
public class ResetServiceTest extends BaseTest {

    @Resource
    ResetService resetService;

    @Test
    public  void reset(){

        resetService.reset("1");

        resetService.reset("testresettoken");
    }
}
