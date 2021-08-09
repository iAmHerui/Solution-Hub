package com.h3c.solutionhub.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ScheduledTest {

//    @Test
//    public void scheduledTest() {
//        log.info("cron: "+System.currentTimeMillis());
//        scheduled();
//    }

//    @Scheduled(cron = "0/5 * * * * ? *")
//    public void scheduled1() {
//        log.info("cron: "+System.currentTimeMillis());
//        log.info("Current Thread : {}",Thread.currentThread().getName());
//    }

    @Scheduled(fixedRate = 5000)
    public void scheduled() {
        log.info("Current Thread : {}",Thread.currentThread().getName());
        log.info("cron: "+System.currentTimeMillis());
    }

//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//    private List<Integer> index = Arrays.asList(6, 6, 2, 3);
//    int i = 0;
//
//    // 这个方法验证了fixedRate的一个坑，内部逻辑不执行完成的话，fixedRate不会去抢时间戳。应该是单线程的原因
//    @Scheduled(fixedRate = fixedRateTime)
//    public void reportCurrentTimeWithFixedRate() {
//        log.info("Current Thread : {}",Thread.currentThread().getName());
//        if (i == 0) {
//            log.info("Start time is {}", dateFormat.format(new Date()));
//        }
//        if (i < 4) {
//            try {
//                TimeUnit.SECONDS.sleep(index.get(i));
//                log.info("Fixed Rate Task : The time is now {}", dateFormat.format(new Date()));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            i++;
//        }
//    }
}
