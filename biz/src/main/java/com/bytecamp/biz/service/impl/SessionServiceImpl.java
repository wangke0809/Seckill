package com.bytecamp.biz.service.impl;

import com.bytecamp.biz.service.SessionService;
import com.bytecamp.dao.SessionsMapper;
import com.bytecamp.model.Sessions;
import com.bytecamp.model.SessionsSearch;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangke
 * @description: SessionServiceImpl
 * @date 2019-08-09 18:24
 */
@Service
public class SessionServiceImpl implements SessionService {

    @Resource(name="sessionsMapper")
    private SessionsMapper _mapper;

    /**
     * 获取所有 session 数据
     *
     * @return
     */
    @Override
    public List<Sessions> getAll() {
        SessionsSearch search = new SessionsSearch();
        return _mapper.selectByExample(search);
    }
}
