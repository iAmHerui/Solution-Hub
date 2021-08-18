package com.h3c.solutionhub.service.impl;

import com.h3c.solutionhub.common.AsyncUtil;
import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.entity.ProjectBo;
import com.h3c.solutionhub.mapper.ProjectManagementMapper;
import com.h3c.solutionhub.service.NodesManagementService;
import com.h3c.solutionhub.service.ProjectManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProjectManagementServiceImpl implements ProjectManagementService {

    @Autowired
    NodesManagementService nodesManagementService;

    @Autowired
    ProjectManagementMapper projectManagementMapper;

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

    @Override
    public List<ProjectBo> getProjectList() {

        // 查询工程信息
        List<ProjectBo> projectList = projectManagementMapper.selectProjectList();

        // 查询工程和产品关联信息
        for(ProjectBo project : projectList) {
            List<String> productList = projectManagementMapper.selectProductName(project.getProjectName());
            project.setProjectProductList(productList);
        }

        return projectList;
    }

    @Override
    public Boolean addProject(ProjectBo project) {

        // 添加工程信息
        projectManagementMapper.insertProject(
                project.getProjectName(),
                project.getProjectDescribe());

        // 添加工程和产品关系
        List<String> productList = project.getProjectProductList();
        for(String productName : productList) {
            projectManagementMapper.insertRefProjectProduct(project.getProjectName(),productName);
        }

        return true;
    }

    @Override
    public Boolean editProject(ProjectBo project) {
        return projectManagementMapper.updateProject(
                project.getProjectName(),
                project.getProjectDescribe());
    }

    @Override
    public Boolean deleteProject(int projectId) {

        String projectName = projectManagementMapper.getProjectNameById(projectId);

        // 删除工程信息
        projectManagementMapper.deleteProject(projectId);

        // 删除工程和产品关联信息
        return projectManagementMapper.deleteRefProjectProduct(projectName);
    }
}
