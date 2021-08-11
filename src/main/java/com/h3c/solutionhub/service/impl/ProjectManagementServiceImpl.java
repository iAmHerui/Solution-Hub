package com.h3c.solutionhub.service.impl;

import com.h3c.solutionhub.common.AsyncUtil;
import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.service.NodesManagementService;
import com.h3c.solutionhub.service.ProjectManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ProjectManagementServiceImpl implements ProjectManagementService {

    @Autowired
    NodesManagementService nodesManagementService;

    @Autowired
    AsyncUtil asyncUtil;

    @Override
    public Boolean projectDeploy(List<NodeBo> nodeList,String productVersion,String hostPoolName, String clusterName)  {

        log.info("---------- 工程部署 BEGIN ----------");
        log.info("Current Thread : {}",Thread.currentThread().getName());

        // 1.部署节点
        for(NodeBo node:nodeList) {
            nodesManagementService.deploySingleNode(node,productVersion);
        }

        // 2.配置集群
        try {
            asyncUtil.asyncDeployCluster(hostPoolName, clusterName, nodeList);
            log.info("---------- 工程部署 END ----------");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
