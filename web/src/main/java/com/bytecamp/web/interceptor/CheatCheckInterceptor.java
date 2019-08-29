package com.bytecamp.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bytecamp.web.cheat.impl.UserIpCheatingCheck;
import com.bytecamp.web.cheat.impl.UserReuqestPathCheatingCheck;
import com.bytecamp.web.cheat.impl.UserSessionCheatingCheck;
import com.bytecamp.web.dto.RequestDTO;
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

/**
 * @author wangke
 * @description: UserInterceptor
 * @date 2019-08-26 17:56
 */
@Slf4j
@Component
public class CheatCheckInterceptor extends HandlerInterceptorAdapter {

    // TODO: 从 IOC 中获取所有 CheatingCheck 子类对象，按优先级排序，就不用每次添加一个作弊策略这里再手动添加了

    @Resource
    UserSessionCheatingCheck userSessionCheatingCheck;

    @Resource
    UserIpCheatingCheck userIpCheatingCheck;

    @Resource
    UserReuqestPathCheatingCheck userReuqestPathCheatingCheck;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        String uri = httpServletRequest.getRequestURI();


        if (uri.equals("/resgitet22222222223") || uri.equals("/reset")) {
            return true;
        }

//        if (uri.equals("/product") || uri.equals("/order") || uri.equals("/pay")
//                || uri.equals("/result")) {
//
//        } else {
//            log.error("请求不存在的接口");
//            httpServletResponse.setStatus(403);
//            return false;
//        }

        String ua = httpServletRequest.getHeader("User-Agent");

//         ua 反作弊判断，正常情况下在 Nginx 层已经被过滤
        if (StringUtils.isEmpty(ua) || ua.contains("spider")) {
            log.error("ua 为空");
            httpServletResponse.setStatus(403);
            return false;
        }

        Integer uid = null;
        String ip = httpServletRequest.getHeader("X-Forwarded-For");
        String session = httpServletRequest.getHeader("sessionid");

//        if (ip == null) {
//            httpServletResponse.setStatus(403);
//            log.error("请求 ip 为空");
//            // TODO: 正式时打开
//            return false;
//        }

        // GET 方法中取 uid 与 POST json 中 获取 uid
        JSONObject jsonObject = null;
        if ("GET".equals(httpServletRequest.getMethod())) {
            String uidStr = httpServletRequest.getParameter("uid");
            if (uidStr != null) {
                try {
                    uid = Integer.valueOf(uidStr);
                } catch (Exception e) {
                    httpServletResponse.setStatus(403);
                    log.error("uid 解析失败", e);
                    return false;
                }
            }
        } else if ("POST".equals(httpServletRequest.getMethod())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpServletRequest.getInputStream()));
            String var1 = "";
            StringBuffer var2 = new StringBuffer();
            while ((var1 = reader.readLine()) != null) {
                var2.append(var1);
            }
            if (!var2.equals("")) {
                jsonObject = JSON.parseObject(var2.toString());
                if (jsonObject == null) {
                    httpServletResponse.setStatus(403);
                    log.error("post 请求但是没请求数据");
                    return false;
                }
                // 读取 post 请求流后，通过这种方式把 post 数据传递到控制器
                httpServletRequest.setAttribute("post", jsonObject);
                try {
                    uid = jsonObject.toJavaObject(UidQuery.class).getUid();
                    if (uid != null && (uid < 174993 || uid > 252693637)) {
                        httpServletResponse.setStatus(403);
                        log.error("uid 范围异常");
                        return false;
                    }
                } catch (Exception e) {
                    httpServletResponse.setStatus(403);
                    log.error("uid 解析失败", e);
                    return false;
                }
            }

        }

        RequestDTO dto = new RequestDTO();

        dto.setIp(ip);
        dto.setPost(jsonObject);
        dto.setSession(session);
        dto.setUa(ua);
        dto.setUri(uri);
        dto.setUid(uid);

        if (userSessionCheatingCheck.check(dto)) {
            httpServletResponse.setStatus(403);
            return false;
        }


        return true;
    }
}
