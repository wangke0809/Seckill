package com.bytecamp.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bytecamp.biz.service.RedisService;
import com.bytecamp.biz.util.RedisKeyUtil;
import com.bytecamp.web.query.UidQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * @author wangke
 * @description: UserInterceptor
 * @date 2019-08-26 17:56
 */
@Slf4j
@Component
public class UserInterceptor extends HandlerInterceptorAdapter {

    @Resource
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {

        String ua = httpServletRequest.getHeader("User-Agent");
        // ua 反作弊判断，正常情况下在 Nginx 层已经被过滤
        if (StringUtils.isEmpty(ua) || ua.contains("spider")) {
            httpServletResponse.setStatus(403);
            return false;
        }

        Integer uid = null;
        String ip = httpServletRequest.getHeader("X-Forwarded-For");
        String session = httpServletRequest.getHeader("sessionid");

        // GET 方法中取 uid 与 POST json 中 获取 uid
        if ("GET".equals(httpServletRequest.getMethod())) {
            String uidStr = httpServletRequest.getParameter("uid");
            if (uidStr != null) {
                uid = Integer.valueOf(uidStr);
            }
        } else if ("POST".equals(httpServletRequest.getMethod())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpServletRequest.getInputStream()));
            String var1 = "";
            String var2 = "";
            while ((var1 = reader.readLine()) != null) {
                var2 += var1;
            }
            if (!var2.equals("")) {
                JSONObject jsonObject = JSON.parseObject(var2);
                // 读取 post 请求流后，通过这种方式把 post 数据传递到控制器
                httpServletRequest.setAttribute("post", jsonObject);
                uid = jsonObject.toJavaObject(UidQuery.class).getUid();
            }

        }

        // uid 不为空时判断是否和 session 符合
        if (uid != null) {

            if (!StringUtils.isEmpty(session)) {
                try {
                    String auth_uid = redisService.hget(RedisKeyUtil.SESSION, session);
                    if (!StringUtils.isEmpty(auth_uid)) {
                        if (!Integer.valueOf(auth_uid).equals(uid)) {
                            log.info("{} 用户身份验证「未」通过", uid);
                            httpServletResponse.setStatus(403);
                            return false;
                        } else {
                            log.info("{} 用户身份验证通过", uid);
                        }
                    } else {
                        log.info("根据 session {} 没有找到对应用户", session);
                    }
                } catch (Exception e) {
                    log.error("session 验证失败", e);
                }
            } else {
                log.info("uid: {} 请求 session 为空", uid);
            }
        } else {
            log.info("请求未包含 uid");
        }
        return true;
    }
}
