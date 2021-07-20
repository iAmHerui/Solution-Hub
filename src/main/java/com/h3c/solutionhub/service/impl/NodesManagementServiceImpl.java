package com.h3c.solutionhub.service.impl;

import com.h3c.solutionhub.common.RestTemplateTool;
import com.h3c.solutionhub.entity.DhcpBO;
import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.mapper.FileManagementMapper;
import com.h3c.solutionhub.mapper.NodesManagementMapper;
import com.h3c.solutionhub.service.NodesManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

@Service
public class NodesManagementServiceImpl implements NodesManagementService {

    @Autowired
    private NodesManagementMapper nodesManagementMapper;

    @Autowired
    private FileManagementMapper fileManagementMapper;

    RestTemplateTool restTemplateTool = new RestTemplateTool();

    @Value("${tempFilePath}")
    private String tempFilePath;

    @Value("${dhcpFilePath}")
    private String dhcpFilePath;

    @Value("${grubFilePath}")
    private String grubFilePath;

    @Value("${mountShell}")
    private String mountShell;

    @Override
    public List<NodeBo> getNodeList() {
        return nodesManagementMapper.getNodeList();
    }

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
                nodeBo.getManagementMask(),
                nodeBo.getBusinessMask(),
                nodeBo.getManagementGateway(),
                nodeBo.getBusinessGateway(),
                nodeBo.getStorageIP(),
                nodeBo.getStorageMask(),
                nodeBo.getStorageGateway());
    }

    @Override
    public Boolean editNode(NodeBo nodeBo) {
        return nodesManagementMapper.editNodeInfo(
                nodeBo.getNodeName(),
                nodeBo.getNodeHDMIP(),
                nodeBo.getNodeType(),
                nodeBo.getNodeStatus(),
                nodeBo.getNodeHDMPaasword(),
                nodeBo.getManagementIP(),
                nodeBo.getBusinessIP(),
                nodeBo.getManagementMask(),
                nodeBo.getBusinessMask(),
                nodeBo.getManagementGateway(),
                nodeBo.getBusinessGateway(),
                nodeBo.getStorageIP(),
                nodeBo.getStorageMask(),
                nodeBo.getStorageGateway());
    }

    @Override
    public Boolean deleteNode(String nodeName) {
        return nodesManagementMapper.deleteNodeInfo(nodeName);
    }

    @Override
    public Boolean deployNode(
            String productType,
            String productVersion,
            List<NodeBo> nodes) {

        // 获取当前DHCP地址
        DhcpBO dhcpBO = nodesManagementMapper.selectDHCPInfo();

        for(NodeBo node:nodes) {
            // 1.获取token
            String token = getToken(node.getManagementIP());
            node.setToken(token);

            // 2.获取管理节点mac
            String mac = getManageNodeMac(node.getManagementIP(),token);
            node.setManagementMAC(mac);
        }

        // 3.生成配置文件dhcpd.conf
        createConfFile(dhcpBO.getDhcpIPPond(),dhcpBO.getDhcpMask(),nodes);

//        // 4.创建子目录（/var/www/html/UUID/）执行mount
//        execLinuxCommand(mountShell);

        // 5.生成配置文件 grub.cfg-宿主机IP16进制
        createGrubConfFile(productType,productVersion);

        for(NodeBo node:nodes) {
            // 6.PXE模式执行
            startPXE(node.getManagementIP(),node.getToken());

            // 7.重启
            reboot(node.getManagementIP(),node.getToken());
        }
        return true;
    }

    @Override
    public Boolean addDHCPInfo(String dhcpIPPond, String dhcpMask) {
        if(nodesManagementMapper.isDhcpExist()>0) {
            nodesManagementMapper.deleteDHCPInfo();
            nodesManagementMapper.insertDHCPInfo(dhcpIPPond,dhcpMask);
        } else {
            nodesManagementMapper.insertDHCPInfo(dhcpIPPond,dhcpMask);
        }
        return true;
    }

    @Override
    public DhcpBO getDHCPInfo() {
        return nodesManagementMapper.selectDHCPInfo();
    }


    private String getToken(String nodeMangeIP) {
        String url = "https://"+nodeMangeIP+"/redfish/v1/SessionService/Sessions";

        HashMap<String, Object> map = new HashMap<>();
        map.put("UserName","admin");
        map.put("Password","Password@_");

        ResponseEntity responseEntity =restTemplateTool.sendHttps(url,map, HttpMethod.POST,null);

        return responseEntity.getHeaders().get("X-Auth-Token").get(0);
    }

    private String getManageNodeMac(String nodeMangeIP,String token) {
        String url = "https://"+nodeMangeIP+"/redfish/v1/Chassis/1/NetworkAdapters/PCIeSlot1/NetworkPorts/1";

        ResponseEntity responseEntity =restTemplateTool.sendHttps(url,null, HttpMethod.GET,token);


        String responseBody = responseEntity.getBody().toString();
        return responseBody.substring(responseBody.indexOf("[")+1,responseBody.lastIndexOf("]"));
    }

    private void startPXE(String nodeMangeIP,String token) {
        String url = "https://"+nodeMangeIP+"/redfish/v1/Systems/1";

        HashMap<String, Object> map = new HashMap<>();
        map.put("AssetTag","solution_hub");
        map.put("HostName","solution_hub");
        HashMap<String, Object> childMap = new HashMap<>();
        childMap.put("BootSourceOverrideMode","UEFI");
        childMap.put("BootSourceOverrideTarget","Pxe");
        childMap.put("BootSourceOverrideEnabled","Once");

        map.put("Boot",childMap);

//        restTemplateTool.sendHttps(url,map,HttpMethod.PATCH,token);
        restTemplateTool.sendHttpsPatch(url,map,token);


    }

    private void reboot(String nodeMangeIP,String token) {
        String url = "https://"+nodeMangeIP+"/redfish/v1/Systems/system_id/Actions/ComputerSystem.Reset";

        HashMap<String, Object> map = new HashMap<>();
        map.put("ResetType","ForceRestart");

        restTemplateTool.sendHttps(url,map,HttpMethod.PATCH,token);
    }

    /**
     * 生成配置文件dhcpd.conf
     */
    private void createConfFile(
            String dhcpIPPond,
            String dhcpMask,
            List<NodeBo> nodeList) {
        String filePath = dhcpFilePath;
        File file = new File(filePath);
        String confInfo ="";
        if(!file.exists()) {
            confInfo =
                    "default-lease-time 600;\n" +
                            "max-lease-time 7200;\n" +
                            "subnet " + dhcpIPPond + " netmask " + dhcpMask + " {\n" +
                            "filename \"BOOTX64.EFI\"\n" +
                            "next-server " + hostIP() + ";\n" +
                            "}\n";
        }
        for(NodeBo nodeBo:nodeList) {
            String mac = nodeBo.getManagementMAC();
            String ip = nodeBo.getManagementIP();
            String confNodeInfo =
                    "host "+nodeBo.getNodeName()+ " {\n"+
                            "    hardware ethernet "+mac+";\n"+
                            "    fixed-address "+ip+";\n"+
                            "}\n";
            confInfo=confInfo+confNodeInfo;
        }
        createFile(confInfo,filePath);
    }

    /**
     * 生成配置文件grub.cfg
     */
    private void createGrubConfFile(String productType, String productVersion) {

        String isoName = fileManagementMapper.getISOName(productType,productVersion);
        // iso所在目录 版本号+iso前缀名称
        String prefixName = isoName.substring(0,isoName.lastIndexOf("."));

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
                        "       inst.stage2=http://"+hostIP()+"/"+prefixName+"\n"+
                        "       inst.ks=http://"+hostIP()+"/ks/"+productVersion+"/ks/ks-auto.cfg"+"\n"+
                        "       net.ifnames=0\n"+
                        "       biosdevname=0\n"+
                        "       quiet\n"+
                        "       initrdefi /images/pxeboot/initrd.img\n"+
                        "}\n";

        createFile(fileInfo,grubFilePath+"-"+to16(hostIP()));
    }

    private void createFile(String fileInfo,String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file,true),"UTF-8");
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

    private String hostIP() {
        InetAddress ip4 = null;
        try {
            ip4 = Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip4.getHostAddress();
    }

}
