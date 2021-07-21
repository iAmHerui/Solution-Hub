package com.h3c.solutionhub.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.solutionhub.entity.DhcpBO;
import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.service.NodesManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/nodeAdd")
    public Boolean nodeAdd(NodeBo nodeBo) {
//        /** test **/
//        nodeBo.setNodeName("cas-node1");
//        nodeBo.setNodeHDMIP("172.17.0.2");
//        nodeBo.setNodeType("CAS");
        nodeBo.setNodeStatus("空闲");
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

    @ApiOperation(value = "节点编辑",notes = "节点编辑")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PutMapping(value = "/nodeEdit")
    public Boolean nodeEdit(NodeBo nodeBo) {
        return nodesManagementService.editNode(nodeBo);
    }


    @ApiOperation(value = "节点删除",notes = "节点删除")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/nodeDelete")
    public Boolean nodeDelete(String nodeName) {
        return nodesManagementService.deleteNode(nodeName);
    }

    @ApiOperation(value = "节点部署",notes = "节点部署")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/nodeDeploy")
    public Boolean nodeDeploy(@RequestBody Map<String,Object> params) {

        String productType = params.get("productType").toString();
        String productVersion = params.get("productVersion").toString();
        String nodeListString = params.get("nodes").toString();

        JSONArray jsonArray = JSONArray.parseArray(nodeListString);
        List<NodeBo> nodes = new ArrayList<>();
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject nodeBo = jsonArray.getJSONObject(i);
            NodeBo node = new NodeBo();
            node.setManagementIP(nodeBo.getString("managementIP"));
            node.setNodeName(nodeBo.getString("nodeName"));
            nodes.add(node);
        }
        return nodesManagementService.deployNode(productType,productVersion,nodes);
    }
    
    @ApiOperation(value = "查看DHCP地址",notes = "查看DHCP地址")
    @CrossOrigin(origins ="*",maxAge =3600)
    @GetMapping(value = "/getDHCPInfo")
    public DhcpBO getDHCPInfo() {
        return nodesManagementService.getDHCPInfo();
    }

    @ApiOperation(value = "添加DHCP地址",notes = "添加DHCP地址")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/addDHCPInfo")
    public Boolean addDHCPInfo(String dhcpIPPond, String dhcpMask) {
        return nodesManagementService.addDHCPInfo(dhcpIPPond,dhcpMask);
    }
}
