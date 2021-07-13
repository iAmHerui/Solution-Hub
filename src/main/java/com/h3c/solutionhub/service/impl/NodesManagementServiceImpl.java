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
import java.util.List;

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
    public Boolean deployNode(
            String dhcpIPPond,
            String dhcpMask,
            List<NodeBo> nodes) {
        // 1.获取token
        String token = getToken();

        // 2.获取管理节点mac
        String mac = getManageNodeMac(token);

        // 3.生成配置文件dhcpd.conf
        // TODO 从数据库查
        createConfFile("","",null);

        // 4.创建子目录（/var/www/html/UUID/）执行mount
        execLinuxCommand("mount ???");

        // 5.生成配置文件 grub.cfg-宿主机IP16进制
        createGrubConfFile("","grub.cfg","");

        // 6.PXE模式执行
        startPXE(token);

        // 7.重启
        reboot(token);

        return true;
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

    private void startPXE(String token) {
        String url = "https://"+deviceIP+"/redfish/v1/Systems/1";

        HashMap<String, Object> map = new HashMap<>();

        HashMap<String, Object> childMap = new HashMap<>();
        childMap.put("BootSourceOverrideMode","UEFI");
        childMap.put("BootSourceOverrideTarget","Pxe");
        childMap.put("BootSourceOverrideEnabled","Once");

        map.put("Boot",childMap);

        restTemplateTool.sendHttps(url,map,HttpMethod.PATCH,token);


    }

    private void reboot(String token) {
        String url = "https://"+deviceIP+"/redfish/v1/Systems/system_id/Actions/ComputerSystem.Reset";

        HashMap<String, Object> map = new HashMap<>();
        map.put("ResetType","ForceRestart");

        restTemplateTool.sendHttps(url,map,HttpMethod.PATCH,token);
    }

    /**
     * 生成配置文件dhcpd.conf
     *
     * @param nodes
     */
    private void createConfFile(
            String dhcpIPPond,
            String dhcpMask,
            List<NodeBo> nodes) {

//        String ip1 = "170.0.0.0";
//        String ip2 = "255.255.255.0";
//        String ip3 = "170.0.0.227";
//        int nodes = 2;
//        String mac1 = "54:2b:de:0b:f1:bc";
//        String ip4 = "170.0.0.142;";

        String confInfo =
                "default-lease-time 600;\n"+
                "max-lease-time 7200;\n"+
                "subnet "+dhcpIPPond+" netmask "+dhcpMask +" {\n"+
                "filename \"BOOTX64.EFI\"\n"+
                "next-server "+"agent所在主机地址"+";\n"+
                "}\n";

//        for(int i=1;i<nodes+1;i++) {
//
//            String mac1 = "54:2b:de:0b:f1:bc";//根据CVK管理IP获取
//            String ip4 = "170.0.0.142";//管理IP
//
//            String confNodeInfo =
//                    "host solution-test"+i+" {\n"+
//                    "    hardware ethernet "+mac1+";\n"+
//                    "    fixed-address "+ip4+";\n"+
//                    "}\n";
//            confInfo=confInfo+confNodeInfo;
//
//        }

        createFile(confInfo,"dhcpd.conf");
    }

    /**
     * 生成配置文件grub.cfg
     *
     * @param ip1
     * @param fileName
     * @param filePath
     */
    private void createGrubConfFile(
            String ip1,
            String fileName,
            String filePath) {

        String fileInfo =
                "set default=\"0\"\n"+
                        "\n"+
                        "function load_video {\n"+
                        "   insmod efi_gop\n"+
                        "   insmod efi_uga\n"+
                        "   insmod video_bochs\n"+
                        "   insmod video_cirrus\n"+
                        "   insmod all_video\n"+
                        "}\n"+
                        "\n"+
                        "load_video\n"+
                        "set gfxpayload=keep\n"+
                        "insmod gzio\n"+
                        "insmod part_gpt\n"+
                        "insmod ext2\n"+
                        "\n"+
                        "set timeout=5\n"+
                        "\n"+
                        "search --no-floppy --set=root -l 'CentOS 7 x86_64'\n"+
                        "\n"+
                        "menuentry 'Install CAS-x86_64' --class fedora --class gnu-linux --class gnu --class os {\n"+
                        "       linuxefi /images/pxeboot/vmlinuz\n"+
                        "       inst.stage2=http://"+ip1+fileName+"\n"+
                        "       inst.ks=http://"+ip1+filePath+"\n"+
                        "       net.ifnames=0\n"+
                        "       biosdevname=0\n"+
                        "       quiet\n"+
                        "       initrdefi /images/pxeboot/initrd.img\n"+
                        "}\n";

        //TODO ip16进制
        createFile(fileInfo,fileName+"-"+to16(ip1));
    }

    private void createFile(String fileInfo,String fileName) {
        try {
            String filePath = confFilePath+fileName;
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

    private void execLinuxCommand(String command) {
        Runtime run = Runtime.getRuntime();
        Process process = null;

        try {
            process = run.exec(command);
            process.waitFor();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private String to16(String ipString) {
            String[] ip=ipString.split("\\.");
            StringBuffer sb=new StringBuffer();
            for (String str : ip) {
                sb.append(Integer.toHexString(Integer.parseInt(str)));
            }
            return sb.toString();
    }



}
