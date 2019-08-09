package com.bytecamp.biz.service;

import com.bytecamp.model.Sessions;

import java.util.List;

/**
 * @author wangke
 * @description: SessionService
 * @date 2019-08-09 18:21
 */
public interface SessionService {

    /**
     * 获取所有 session 数据
     * @return
     */
    public List<Sessions> getAll();
}
