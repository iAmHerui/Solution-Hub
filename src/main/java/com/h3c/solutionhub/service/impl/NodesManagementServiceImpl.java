package com.h3c.solutionhub.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.h3c.solutionhub.common.RestTemplateTool;
import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.mapper.NodesManagementMapper;
import com.h3c.solutionhub.service.NodesManagementService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;

@Service
public class NodesManagementServiceImpl implements NodesManagementService {

    @Value("${deviceIP}")
    private String deviceIP;

    @Value("${uploadFolder}")
    private String confFilePath;

    @Autowired
    private NodesManagementMapper nodesManagementMapper;

    RestTemplateTool restTemplateTool = new RestTemplateTool();

    @Override
    public Boolean insertNode(NodeBo nodeBo) {

        return nodesManagementMapper.insertNodeInfo(
                nodeBo.getNodeName(),
                nodeBo.getNodeHDMIP(),
                nodeBo.getNodeType(),
                nodeBo.getNodeStatus(),
                nodeBo.getNodeHDMPaasword(),
                nodeBo.getManagementIP(),
                nodeBo.getBusinessIP(),
                nodeBo.getManagementGateway(),
                nodeBo.getBusinessGateway());
    }

    @Override
    public Boolean deleteNode(String nodeName) {
        return nodesManagementMapper.deleteNodeInfo(nodeName);
    }

    @Override
    public Boolean deployNode() {
        // 1.获取token
        String token = getToken();

        // 2.获取管理节点mac
        String mac = getManageNodeMac(token);

        // 3.生成配置文件dhcpd.conf
        // TODO 从数据库查
        createConfFile("","","",2);

        // 4.执行mount
        // 5.生成配置文件 grub.cfg-宿主机IP16进制
        // 6.PXE模式执行
        // 7.重启
        return null;
    }


    private String getToken() {
        String url = "https://"+deviceIP+"/redfish/v1/SessionService/Sessions";

        HashMap<String, Object> map = new HashMap<>();
        map.put("UserName","admin");
        map.put("Password","Password@_");

        ResponseEntity responseEntity =restTemplateTool.sendHttps(url,map, HttpMethod.POST,null);

        return responseEntity.getHeaders().get("X-Auth-Token").get(0);
    }

    private String getManageNodeMac(String token) {
        String url = "https://"+deviceIP+"/redfish/v1/Chassis/1/NetworkAdapters/PCIeSlot1/NetworkPorts/1";

        ResponseEntity responseEntity =restTemplateTool.sendHttps(url,null, HttpMethod.GET,token);

        String responseBody = responseEntity.getBody().toString();
        JSONObject resultJson = JSON.parseObject(responseBody);
        return resultJson.getJSONArray("AssociatedNetworkAddresses").get(0).toString();
    }

    private void createConfFile(
            String ip1,
            String ip2,
            String ip3,
            int nodes) {

//        String ip1 = "170.0.0.0";
//        String ip2 = "255.255.255.0";
//        String ip3 = "170.0.0.227";
//        int nodes = 2;
//        String mac1 = "54:2b:de:0b:f1:bc";
//        String ip4 = "170.0.0.142;";

        String confInfo =
                "default-lease-time 600;\n"+
                "max-lease-time 7200;\n"+
                "subnet "+ip1+" netmask "+ip2 +" {\n"+
                "filename \"BOOTX64.EFI\"\n"+
                "next-server "+ip3+";\n"+
                "}\n";

        for(int i=1;i<nodes+1;i++) {

            String mac1 = "54:2b:de:0b:f1:bc";
            String ip4 = "170.0.0.142";

            String confNodeInfo =
                    "host solution-test"+i+" {\n"+
                    "    hardware ethernet "+mac1+";\n"+
                    "    fixed-address "+ip4+";\n"+
                    "}\n";
            confInfo=confInfo+confNodeInfo;

        }

        createFile(confInfo);
    }

    private void createFile(String fileInfo) {
        try {
            String filePath = confFilePath+"dhcpd.conf";
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileInfo);
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void execLinuxCommend() {
        // TODO 执行mount
    }
}
