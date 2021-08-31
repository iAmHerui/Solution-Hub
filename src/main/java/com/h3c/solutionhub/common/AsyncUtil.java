package com.h3c.solutionhub.common;

import com.h3c.solutionhub.entity.NodeBo;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
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

        Date startTime = new Date();
        log.info("配置集群,启动时间: "+startTime);

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

            // 获取当前时间
            Date now = new Date();
            if(now.getTime()-startTime.getTime()>60*60*1000) {
                log.warn("配置集群超时,强制退出！");
                return;
            }
        }

        while (true) {
            Long clusterId = httpClientUtil.getClusterIdByName(managementIp,clusterName);
            // 添加主机
            for(NodeBo node:nodeList) {
                try {
                    if(!node.getNodeDeployStatus()) {
                        Boolean result = httpClientUtil.addHost(nodeManagementUserName, nodeManagementPassword, hostPoolId, clusterId, managementIp, node.getManagementIP());
                        if (result) {
                            // 主机添加成功,修改主机添加状态
                            node.setNodeDeployStatus(true);
                            log.info("---------- node: " + node.getNodeName() + ",主机添加,SUCCESS ----------");
                        } else {
                            log.info("---------- node: " + node.getNodeName() + ",主机添加,FAILURE ----------");
                        }
                    }
                } catch (Exception e) {
                    log.info("---------- node: "+node.getNodeName()+",主机添加,FAILURE ----------");
                }
            }

            // 主机全部添加完成,退出部署
            Boolean isExit = true;
            for(NodeBo node:nodeList) {
                if(!node.getNodeDeployStatus()) {
                    isExit = false;
                }
            }
            if(isExit) {
                log.info("集群部署完成");
                return;
            }

            log.info("---------- 主机仍在部署,等待3min ----------");
            TimeUnit.SECONDS.sleep(180);

            // 获取当前时间
            Date now = new Date();
            if(now.getTime()-startTime.getTime()>60*60*1000) {
                log.warn("配置集群超时,强制退出！");
                return;
            }
        }
    }

//    // 检测部署时长,最大部署时间1h,超时强行退出
//    private void checkTime(Date startTime) {
//        // 获取当前时间
//        Date now = new Date();
//
//        if(now.getTime()-startTime.getTime()>60*60*1000) {
//            log.warn("配置集群超时,强制退出！");
//            System.exit(0);
//        }
//    }


}
