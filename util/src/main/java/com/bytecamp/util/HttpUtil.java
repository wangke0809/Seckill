package com.bytecamp.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

/**
 * @author wangke
 * @description: HttpUtil
 * @date 2019-08-10 14:24
 */
@Slf4j
public class HttpUtil {

    private static final OkHttpClient client = new OkHttpClient();

    public static Response execute(Request request) throws IOException {
        return client.newCall(request).execute();
    }

//    public static void enqueue(Request request, Callback responseCallback) {
//        client.newCall(request).enqueue(responseCallback);
//    }

    public static String postJson(String url, Object json)
            throws IOException {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String str = JSON.toJSONString(json);
        log.info(" [ postJson] url {} json {}", url, str);
        RequestBody body = RequestBody.create(mediaType, str);
        Request request = new Request.Builder().url(url).post(body)
                .addHeader("content-type", "application/json").build();

        Response response = execute(request);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return response.body().string();
    }

    public static String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        Response response = execute(request);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return response.body().string();
    }

}
