package com.bytecamp.web.schedul;

import com.bytecamp.biz.service.RedisService;
import com.bytecamp.biz.service.SessionService;
import com.bytecamp.biz.util.RedisKeyUtil;
import com.bytecamp.model.Sessions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangke
 * @description: 从数据库加载 session 到 redis
 * @date 2019-08-09 18:14
 */
@Component
@Slf4j
public class SessionLoadSchedul {

    @Resource
    RedisService redisService;

    @Resource
    SessionService sessionService;

    @Scheduled(fixedDelay = 5000)
    public void task() throws Exception{
        // 如果 redis 中不存在 session ，从数据库中加载
        if (!redisService.exists(RedisKeyUtil.SESSION)) {
            // redis 重启 或者 集群竞争时，避免重复导入
            Thread.sleep(10000);
            if(redisService.exists(RedisKeyUtil.SESSION)){
                return;
            }
            Long start = System.currentTimeMillis();
            log.info("开始从数据库中加载 session");
            final List<Sessions> all = sessionService.getAll();
            log.info("共需要加载 {} 条数据", all.size());
            for(Sessions s : all){
                redisService.hset(RedisKeyUtil.SESSION, s.getSessionId(), s.getId().toString());
            }
            log.info("结束从数据库中加载 session, 用时 {} s", (System.currentTimeMillis()-start)/1000.0);
        }
    }
}
