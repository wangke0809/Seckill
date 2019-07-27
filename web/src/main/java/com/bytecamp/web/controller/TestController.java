package com.bytecamp.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wangke
 * @description: 测试控制器
 * @date 2019-07-27 02:11
 */
@Controller
public class TestController {

    @ResponseBody
    @RequestMapping("/test")
    public String test() {
        return "<h1>Hello World!</h1>";
    }
}
