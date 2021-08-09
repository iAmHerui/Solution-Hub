package com.h3c.solutionhub.common;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class AsyncTest {

    // TODO:异步执行节点部署。


    private void nodeDeploy() {
        log.info("Current Thread : {}",Thread.currentThread().getName());
        log.info("开始节点部署");
        try {
            TimeUnit.SECONDS.sleep(7);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("开始节点完成");
    }
}
