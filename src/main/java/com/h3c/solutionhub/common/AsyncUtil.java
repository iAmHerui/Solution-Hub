package com.h3c.solutionhub.common;

import com.h3c.solutionhub.entity.NodeBo;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AsyncUtil {

    @Value("${nodeManagementUserName}")
    private String nodeManagementUserName;

    @Value("${nodeManagementPassword}")
    private String nodeManagementPassword;

    @Async
    public void asyncDeployCluster(String hostPoolName, String clusterName, List<NodeBo> nodeList) throws Exception{

        log.info("---------- 配置集群 BEGIN ----------");
        log.info("Current Thread : {}",Thread.currentThread().getName());

        HttpClientUtil httpClientUtil = new HttpClientUtil();
        Long hostPoolId = 0L;

        // 获取CVM managementIP
        String managementIp = "";
        for(NodeBo node:nodeList) {
            if(node.getNodeType().equals("CAS_CVM")) {
                managementIp = node.getManagementIP();
            }
        }

        while (true) {
            log.info("---------- CVM正在部署,sleep 3min ----------");
            TimeUnit.SECONDS.sleep(180);
            if(httpClientUtil.isConnect(managementIp)) {
                log.info("---------- CAS集群已连通,开始初始化 ----------");

                for(NodeBo node:nodeList) {
                    if(node.getNodeType().equals("CAS_CVM")) {
                        httpClientUtil.addHostPool(managementIp,hostPoolName);
                        log.info("---------- 主机池: "+hostPoolName+",已创建 ----------");

                        hostPoolId = httpClientUtil.getHostPoolIdByName(managementIp,hostPoolName);
                        log.info("---------- 主机池ID: "+hostPoolId+",已获取 ----------");

                        httpClientUtil.addCluster(managementIp,hostPoolId,clusterName);
                        log.info("---------- 集群: "+clusterName+",已创建 ----------");
                    }
                }
                break;
            }
        }

        while (true) {
            Long clusterId = httpClientUtil.getClusterIdByName(managementIp,clusterName);
            // 添加主机
            for(NodeBo node:nodeList) {
                try {
                    Boolean result = httpClientUtil.addHost(nodeManagementUserName, nodeManagementPassword, hostPoolId, clusterId, managementIp,node.getManagementIP());
                    if(result) {
                        nodeList.remove(node);
                        log.info("---------- node: "+node.getNodeName()+",主机添加,SUCCESS ----------");
                    }else {
                        log.info("---------- node: "+node.getNodeName()+",主机添加,FAILURE ----------");
                    }
                    if(nodeList.size()<=0) {
                        log.info("---------- 配置集群 END ----------");
                        return;
                    }
                } catch (Exception e) {
                    log.info("---------- node: "+node.getNodeName()+",主机添加,FAILURE ----------");
                }
            }
            log.info("---------- 主机仍在部署,等待3min ----------");
            TimeUnit.SECONDS.sleep(180);
        }
    }


}
