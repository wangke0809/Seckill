package com.bytecamp.web.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: InterceptorConfig
 * @date 2019-08-26 18:19
 */
@Component
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    LogInterceptor logInterceptor;
    @Resource
    CheatCheckInterceptor cheatCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 全链路日志
        registry.addInterceptor(logInterceptor).addPathPatterns("/**");
        // 用户 session 检查
        registry.addInterceptor(cheatCheckInterceptor).addPathPatterns("/**");


    }
}
