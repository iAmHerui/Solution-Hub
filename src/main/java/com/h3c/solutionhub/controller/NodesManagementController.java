package com.h3c.solutionhub.controller;

import com.h3c.solutionhub.common.JsonResult;
import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.service.NodesManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "节点管理",tags = "节点管理")
@RestController
@CrossOrigin
@RequestMapping(value = "/nodesManagement")
public class NodesManagementController {

    private static final Logger log = LoggerFactory.getLogger(NodesManagementController.class);

    @Autowired
    NodesManagementService nodesManagementService;

    @ApiOperation(value = "节点添加",notes = "节点添加")
    @PostMapping(value = "/nodeAdd")
    public Boolean nodeAdd(NodeBo nodeBo) {
        return nodesManagementService.insertNode(nodeBo);
    }

    @ApiOperation(value = "节点删除",notes = "节点删除")
    @PostMapping(value = "/nodeDelete")
    public Boolean nodeDelete(String nodeName) {
        return nodesManagementService.deleteNode(nodeName);
    }

    @ApiOperation(value = "节点部署",notes = "节点部署")
    @PostMapping(value = "/nodeDeploy")
    public Boolean nodeDeploy() {
        return nodesManagementService.deployNode();
    }

}
