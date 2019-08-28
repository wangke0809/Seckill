package com.bytecamp.web.cheat.impl;

import com.bytecamp.biz.service.RedisService;
import com.bytecamp.biz.util.RedisKeyUtil;
import com.bytecamp.web.cheat.CheatingCheck;
import com.bytecamp.web.dto.RequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author wangke
 * @description: UserSessionCheatingCheck
 * @date 2019-08-27 23:53
 */
@Component
@Slf4j
public class UserSessionCheatingCheck implements CheatingCheck {

    @Resource
    RedisService redisService;

    /**
     * 返回真证明作弊
     *
     * @param dto
     * @return
     */
    @Override
    public Boolean check(RequestDTO dto) {
        // session 判断
        String session = dto.getSession();
        String auth_uid = null;
        if (!StringUtils.isEmpty(session)) {
            auth_uid = redisService.hget(RedisKeyUtil.SESSION, session);
            if (StringUtils.isEmpty(auth_uid)) {
                log.info("根据 session {} 没有找到对应用户", session);
                if (dto.getUri().equals("/reset")) {
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            return true;
        }
        // uid 不为空时判断是否和 session 符合
        if (dto.getUid() != null) {
            Integer uid = dto.getUid();
            if (!StringUtils.isEmpty(session)) {
                try {
                    if (!Integer.valueOf(auth_uid).equals(uid)) {
                        log.info("{} 用户身份验证「未」通过", uid);
                        return true;
                    } else {
                        log.info("{} 用户身份验证通过", uid);
                    }
                } catch (Exception e) {
                    log.error("session 验证失败", e);
                    return true;
                }
            } else {
                // 测试先不处理
                log.info("uid: {} 请求 session 为空", uid);
            }
        } else {
//            log.info("请求未包含 uid");
            dto.setUid(Integer.valueOf(auth_uid));
        }

        return false;
    }
}
