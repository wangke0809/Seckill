package com.bytecamp.web.schedul;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author wangke
 * @description: 测试获取机器 id
 * @date 2019-08-27 10:55
 */
@Slf4j
//@Component
public class MachineTestSchedul {

    @Value("${seckill.machine-id}")
    private int machineId;

    @Scheduled(fixedDelay = 5000)
    public void task() throws Exception{
        log.info("machne-id: " + machineId);
    }
}
