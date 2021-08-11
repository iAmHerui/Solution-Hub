package com.h3c.solutionhub.service;

import com.h3c.solutionhub.entity.NodeBo;

import java.util.List;

public interface ProjectManagementService {

    Boolean projectDeploy(List<NodeBo> nodeList,String productVersion,String hostPoolName, String clusterName);
}
