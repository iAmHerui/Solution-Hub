package com.h3c.solutionhub.common;

import com.h3c.solutionhub.entity.NodeBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AsyncUtil {

    @Async
    public void asyncDeployCluster(String hostPoolName, String clusterName, List<NodeBo> nodeList) throws Exception{

        log.info("---------- 配置集群 BEGIN ----------");
        log.info("Current Thread : {}",Thread.currentThread().getName());

        HttpClientUtil httpClientUtil = new HttpClientUtil();

        // 获取CVM managementIP
        String managementIp = "";
        for(NodeBo node:nodeList) {
            if(node.getNodeType().equals("CAS_CVM")) {
                managementIp = node.getManagementIP();
            }
        }

        while (true) {
            TimeUnit.SECONDS.sleep(10);
            if(httpClientUtil.isConnect(managementIp)) {
                log.info("---------- CAS集群已连通,开始初始化 ----------");
                Long hostPoolId = 0L;
                Long clusterId = 0L;
                for(NodeBo node:nodeList) {
                    if(node.getNodeType().equals("CAS_CVM")) {
                        httpClientUtil.addHostPool(managementIp,hostPoolName);
                        log.info("---------- 主机池: "+hostPoolName+",已创建 ----------");

                        hostPoolId = httpClientUtil.getHostPoolIdByName(managementIp,hostPoolName);
                        log.info("---------- 主机池ID: "+hostPoolId+",已获取 ----------");

                        httpClientUtil.addCluster(managementIp,hostPoolId,clusterName);
                        log.info("---------- 集群: "+clusterName+",已创建 ----------");

                        clusterId = httpClientUtil.getClusterIdByName(managementIp,clusterName);
                        log.info("---------- 集群ID: "+clusterId+",已获取 ----------");
                    }
                }

                // 添加主机
                for(NodeBo node:nodeList) {
                    // TODO 只需要添加CVK吗？
                    if(node.getNodeType().equals("CAS_CVK")) {
                        httpClientUtil.addHost(node.getManagementUserName(),node.getManagementPassword(),hostPoolId,clusterId,node.getManagementIP());
                        log.info("---------- node: "+node.getNodeName()+",主机添加,SUCCESS ----------");
                    }
                }
                log.info("---------- 配置集群 END ----------");
                return;
            }
            log.info("---------- CAS集群未连通,sleep 10s ----------");
        }
    }
}
