package com.bytecamp.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author wangke
 * @description: SpringBoot 入口
 * @date 2019-07-27 02:07
 */
@SpringBootApplication
@MapperScan("com.bytecamp.dao")
@EnableTransactionManagement
@ServletComponentScan
@ComponentScan(basePackages={"com.bytecamp"})
//@EnableScheduling
//@EnableJms
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
