package com.h3c.solutionhub.controller;

import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.service.NodesManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "节点管理",tags = "节点管理")
@RestController
@CrossOrigin
@RequestMapping(value = "/nodesManagement")
public class NodesManagementController {

    @Autowired
    NodesManagementService nodesManagementService;

    @ApiOperation(value = "节点列表",notes = "节点列表")
    @GetMapping(value = "nodeList")
    public List<NodeBo> nodeList() {
        return nodesManagementService.getNodeList();
    }

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

    /**
     * 节点部署
     *
     * @param dhcpIPPond
     * @param dhcpMask
     * @param nodes（NodeName、ManagementIP）
     * @return
     */
    @ApiOperation(value = "节点部署",notes = "节点部署")
    @PostMapping(value = "/nodeDeploy")
    public Boolean nodeDeploy(
            String dhcpIPPond,
            String dhcpMask,
            String productType,
            String productVersion,
            List<NodeBo> nodes) {
        return nodesManagementService.deployNode(dhcpIPPond,dhcpMask,productType,productVersion,nodes);
    }

}
