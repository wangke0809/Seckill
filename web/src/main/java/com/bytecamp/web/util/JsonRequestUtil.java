package com.bytecamp.web.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wangke
 * @description: RequestUtil
 * @date 2019-08-10 01:30
 */
public class JsonRequestUtil {

    public static <T> T getPostJson(HttpServletRequest request, Class<T> tClass){
        JSONObject jsonObject = (JSONObject)request.getAttribute("post");
        if(jsonObject == null){
            return null;
        }
        return jsonObject.toJavaObject(tClass);
    }
}
