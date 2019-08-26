package com.bytecamp.web.interceptor;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author wangke
 * @description: 全链路日志
 * @date 2019-08-26 17:56
 */
@Component
public class LogInterceptor extends HandlerInterceptorAdapter {
    /**
     * 会话ID
     */
    private final static String SESSION_KEY = "sessionId";


    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String token = UUID.randomUUID().toString().replace("-", "");
        MDC.put(SESSION_KEY, token);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {
        MDC.remove(SESSION_KEY);
    }
}
