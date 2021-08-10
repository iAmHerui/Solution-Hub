package com.h3c.solutionhub.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AsyncUtil {

    @Async
    public void asyncDeployCluster() throws Exception{

        log.info("Current Thread : {}",Thread.currentThread().getName());

        // TODO 判断连通性：不通，一直等待；通，继续执行
        TimeUnit.SECONDS.sleep(5);

        // TODO 初始化集群
        log.info("初始化集群 begin");

        TimeUnit.SECONDS.sleep(10);
        log.info("创建主机池");
        log.info("创建集群");
        log.info("增加主机");

        log.info("初始化集群 Finish");
    }

}
