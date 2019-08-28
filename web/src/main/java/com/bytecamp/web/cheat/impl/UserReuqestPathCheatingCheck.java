package com.bytecamp.web.cheat.impl;

import com.bytecamp.biz.service.RedisHelper;
import com.bytecamp.web.cheat.CheatingCheck;
import com.bytecamp.web.dto.RequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: TUserReuqestPathCheatingCheckODO
 * @date 2019-08-28 11:35
 */
@Component
@Slf4j
public class UserReuqestPathCheatingCheck implements CheatingCheck {

    @Resource
    RedisHelper redisHelper;

    /**
     * 返回真证明作弊
     *
     * @param dto
     * @return
     */
    @Override
    public Boolean check(RequestDTO dto) {
        // TODO: 支付链路路径判断
        if (dto.getUri().equals("/product")) {
            redisHelper.requestProductPathAdd(dto.getUid());
        } else if (dto.getUri().equals("/order")) {
            if (!redisHelper.requestProductPathExists(dto.getUid(), dto.getIp())) {
                log.info("requestProductPathExists 作弊");
                return true;
            }
        }
        return false;
    }
}
