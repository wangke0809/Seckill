package com.bytecamp.web.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author wangke
 * @description: RequestDTO
 * @date 2019-08-27 23:29
 */
@Data
public class RequestDTO {

    String ua;

    String uri;

    String ip;

    String session;

    JSONObject post;

    Integer uid;

}
