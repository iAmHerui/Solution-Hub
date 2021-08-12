package com.h3c.solutionhub.service;

import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.entity.ProjectBo;

import java.util.List;

public interface ProjectManagementService {

    Boolean projectDeploy(List<NodeBo> nodeList,String productVersion,String hostPoolName, String clusterName);

    List<ProjectBo> getProjectList();

    Boolean addProject(ProjectBo project);

    Boolean editProject(ProjectBo project);

    Boolean deleteProject(int projectId);
}
