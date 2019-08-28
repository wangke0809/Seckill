package com.bytecamp.web.cheat.impl;

import com.bytecamp.biz.service.RedisHelper;
import com.bytecamp.web.cheat.CheatingCheck;
import com.bytecamp.web.dto.RequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: UserIpCheatingCheck
 * @date 2019-08-28 10:34
 */
@Component
@Slf4j
public class UserIpCheatingCheck implements CheatingCheck {

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
        if (dto.getUri().equals("/reset")) {
            return false;
        }
        String ip = dto.getIp();
        Integer uid = dto.getUid();
        if (redisHelper.userIpIsChange(uid, ip)) {
            log.error("userIpIsChange 作弊");
            return true;
        }

        if (redisHelper.userIsBlack(uid)) {
            log.error("userIsBlack 作弊");
            return true;
        }

        if (redisHelper.ipIsBlack(ip)) {
            log.error("ipIsBlack 作弊");
            return true;
        }
        return false;
    }
}
