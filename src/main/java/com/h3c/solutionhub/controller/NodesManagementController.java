package com.h3c.solutionhub.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.solutionhub.entity.DhcpBO;
import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.service.NodesManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Slf4j
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
    public String nodeAdd(NodeBo nodeBo) {
        if(nodesManagementService.isNodeExist(nodeBo.getNodeName())) {
            log.info("nodeName不存在重复，开始添加");
            nodeBo.setNodeStatus("空闲");
            Boolean result =  nodesManagementService.insertNode(nodeBo);
            if(result==true) {
                return "节点添加成功";
            } else {
                return "节点添加失败";
            }
        } else {
            log.info("node已存在");
            return "节点已存在，添加失败";
        }
    }

    @ApiOperation(value = "节点编辑",notes = "节点编辑")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PutMapping(value = "/nodeEdit")
    public String nodeEdit(NodeBo nodeBo) {
        Boolean result =  nodesManagementService.editNode(nodeBo);
        if(result==true) {
            return "节点编辑成功";
        } else {
            return "节点编辑失败";
        }
    }


    @ApiOperation(value = "节点删除",notes = "节点删除")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/nodeDelete")
    public String nodeDelete(String nodeName) {
        Boolean result = nodesManagementService.deleteNode(nodeName);
        if(result==true) {
            return "节点删除成功";
        } else {
            return "节点删除失败";
        }
    }

//    @ApiOperation(value = "节点部署",notes = "节点部署")
//    @CrossOrigin(origins ="*",maxAge =3600)
//    @PostMapping(value = "/nodeDeploy")
//    public String nodeDeploy(@RequestBody Map<String,Object> params) {
//
//        String productType = params.get("productType").toString();
//        String productVersion = params.get("productVersion").toString();
//        String nodeListString = params.get("nodes").toString();
//
//        JSONArray jsonArray = JSONArray.parseArray(nodeListString);
//        List<NodeBo> nodes = new ArrayList<>();
//        for(int i=0;i<jsonArray.size();i++) {
//            JSONObject nodeBo = jsonArray.getJSONObject(i);
//            NodeBo node = new NodeBo();
//            node.setNodeId(nodeBo.getInteger("nodeId"));
//            node.setNodeHDMIP(nodeBo.getString("nodeHDMIP"));
//            node.setManagementIP(nodeBo.getString("managementIP"));
//            node.setNodeName(nodeBo.getString("nodeName"));
//
//            node.setManagementMask(nodeBo.getString("managementMask"));
//            node.setManagementGateway(nodeBo.getString("managementGateway"));
//
//            nodes.add(node);
//        }
//        Boolean result = nodesManagementService.deployNode(productType,productVersion,nodes);
//        if(result==true) {
//            return "节点部署成功";
//        } else {
//            return "节点部署失败";
//        }
//    }

    @ApiOperation(value = "查看DHCP地址",notes = "查看DHCP地址")
    @CrossOrigin(origins ="*",maxAge =3600)
    @GetMapping(value = "/getDHCPInfo")
    public DhcpBO getDHCPInfo() {
        return nodesManagementService.getDHCPInfo();
    }

    @ApiOperation(value = "添加DHCP地址",notes = "添加DHCP地址")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/addDHCPInfo")
    public String addDHCPInfo(String dhcpIPPond, String dhcpMask) {
        Boolean result = nodesManagementService.addDHCPInfo(dhcpIPPond,dhcpMask);
        if(result==true) {
            return "DHCP地址添加成功";
        } else {
            return "DHCP地址添加失败";
        }
    }

    @ApiOperation(value = "执行shell",notes = "执行shell")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/execShell")
    public void execShell(String shell) {
        Runtime run = Runtime.getRuntime();
        Process process = null;

        try {
            process = run.exec(shell);
            process.waitFor();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "获取本机IP",notes = "获取本机IP")
    @CrossOrigin(origins ="*",maxAge =3600)
    @GetMapping(value = "/getHostIP")
    public String getHostIP(String eth) {
        String ip = "";
        try {
            Enumeration<?> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) enumeration.nextElement();
                if (!ni.getName().equals(eth)) {
                    continue;
                } else {
                    Enumeration<?> e2 = ni.getInetAddresses();
                    while (e2.hasMoreElements()) {
                        InetAddress ia = (InetAddress) e2.nextElement();
                        if (ia instanceof Inet6Address)
                            continue;
                        ip = ia.getHostAddress();
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
//                System.exit(-1);
        }
        return ip;
    }

    @ApiOperation(value = "获取本机IP2",notes = "获取本机IP2")
    @CrossOrigin(origins ="*",maxAge =3600)
    @GetMapping(value = "/getHostIP2")
    public String getHostIP2() {
        InetAddress ip4 = null;
        try {
            ip4 = Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip4.getHostAddress();
    }

//    @ApiOperation(value = "节点部署Test",notes = "节点部署Test")
//    @GetMapping(value = "/nodeDeployTest")
//    public Boolean nodeDeployTest() {
//        log.info("Current Thread : {}",Thread.currentThread().getName());
//        log.info("开始节点部署");
//        try {
//            TimeUnit.SECONDS.sleep(7);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            return false;
//        }
//        log.info("开始节点完成");
//        return true;
//    }


}
