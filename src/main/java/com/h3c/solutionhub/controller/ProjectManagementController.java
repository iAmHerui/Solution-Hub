package com.h3c.solutionhub.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.entity.ProjectBo;
import com.h3c.solutionhub.service.ProjectManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "工程管理",tags = "工程管理")
@RestController
@CrossOrigin
@RequestMapping(value = "/projectManagement")
public class ProjectManagementController {

    @Autowired
    ProjectManagementService projectManagementService;

    @ApiOperation(value = "工程部署",notes = "工程部署")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/projectDeploy")
    public String projectDeploy(@RequestBody Map<String,Object> params) {

        String hostPoolName = params.get("hostPoolName").toString();
        String clusterName = params.get("clusterName").toString();
        String productVersion = params.get("productVersion").toString();
        String nodeListString = params.get("nodes").toString();

        JSONArray jsonArray = JSONArray.parseArray(nodeListString);
        List<NodeBo> nodes = new ArrayList<>();
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject nodeBo = jsonArray.getJSONObject(i);
            NodeBo node = new NodeBo();
            node.setNodeId(nodeBo.getInteger("nodeId"));
            node.setNodeHDMIP(nodeBo.getString("nodeHDMIP"));
            node.setManagementIP(nodeBo.getString("managementIP"));
            node.setNodeName(nodeBo.getString("nodeName"));
            node.setNodeType(nodeBo.getString("nodeType"));
            node.setManagementUserName(nodeBo.getString("managementUserName"));
            node.setManagementPassword(nodeBo.getString("managementPassword"));
            node.setManagementMask(nodeBo.getString("managementMask"));
            node.setManagementGateway(nodeBo.getString("managementGateway"));

            nodes.add(node);
        }

        Boolean result = projectManagementService.projectDeploy(nodes,productVersion,hostPoolName,clusterName);

        if(result==true) {
            return "工程部署成功";
        } else {
            return "工程部署失败";
        }
    }

    @ApiOperation(value = "工程列表",notes = "工程列表")
    @CrossOrigin(origins ="*",maxAge =3600)
    @GetMapping(value = "/projectList")
    public List<ProjectBo> projectList() {
        return projectManagementService.getProjectList();
    }

    @ApiOperation(value = "添加工程",notes = "添加工程")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/projectAdd")
    public String projectAdd(ProjectBo project) {


        List<String> productList = new ArrayList<>();
        productList.add("CAS");
        project.setProjectProductList(productList);

        Boolean result = projectManagementService.addProject(project);
        if(result==true) {
            return "工程添加成功";
        } else {
            return "工程添加失败";
        }
    }

    @ApiOperation(value = "修改工程",notes = "修改工程")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/projectEdit")
    public String projectEdit(ProjectBo project) {
        Boolean result = projectManagementService.editProject(project);
        if(result==true) {
            return "工程修改成功";
        } else {
            return "工程修改失败";
        }
    }

    @ApiOperation(value = "删除工程",notes = "删除工程")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/projectDelete")
    public String projectDelete(int projectId) {
        Boolean result = projectManagementService.deleteProject(projectId);
        if(result==true) {
            return "工程删除成功";
        } else {
            return "工程删除失败";
        }
    }
}
