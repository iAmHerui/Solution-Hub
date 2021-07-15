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

    /**
     * 节点添加
     *
     * @param nodeBo 除了token，都要传
     * @return
     */
    @ApiOperation(value = "节点添加",notes = "节点添加")
    @PostMapping(value = "/nodeAdd")
    public Boolean nodeAdd(NodeBo nodeBo) {
//        /** test **/
//        nodeBo.setNodeName("cas-node1");
//        nodeBo.setNodeHDMIP("172.17.0.2");
//        nodeBo.setNodeType("CAS");
//        nodeBo.setNodeStatus("空闲");
//        nodeBo.setNodeHDMPaasword("Password@_");
//        nodeBo.setManagementIP("192.168.0.2");
//        nodeBo.setBusinessIP("192.168.1.2");
//        nodeBo.setManagementMask("255.255.255.0");
//        nodeBo.setBusinessMask("255.255.255.0");
//        nodeBo.setManagementGateway("192.168.0.1");
//        nodeBo.setBusinessGateway("192.168.1.1");
//        /** test **/
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
//        /** test **/
//        ) {
//        String dhcpIPPond = "170.0.0.0";
//        String dhcpMask = "255.255.255.0";
//        String productType = "CAS";
//        String productVersion = "E0701";
//
//        List<NodeBo> nodes = new ArrayList<>();
//        NodeBo node = new NodeBo();
//        node.setManagementIP("170.0.0.36");
//        node.setNodeName("cas-node1");
//        nodes.add(node);
//        /** test **/
        return nodesManagementService.deployNode(dhcpIPPond,dhcpMask,productType,productVersion,nodes);
    }

}
