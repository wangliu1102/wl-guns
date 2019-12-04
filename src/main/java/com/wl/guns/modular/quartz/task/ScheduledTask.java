package com.wl.guns.modular.quartz.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 普通定时器
 *
 * @author 王柳
 * @date 2019/11/27 18:42
 */
@Component
@Slf4j
public class ScheduledTask {

    /**
     * 每秒执行一次
     */
//    @Scheduled(cron = "* * * * * ?")
//    @Scheduled(cron = "${jobs.testSchedule}")
    public void testSchedule() {
        log.info("执行测试方法-----无参》》》普通定时器");
    }

    public void testSchedule(String name) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("执行测试方法-----有参》》》普通定时器:" + name);
    }
}
