package com.h3c.solutionhub.service.impl;

import com.h3c.solutionhub.common.HttpClientUtil;
import com.h3c.solutionhub.common.RestTemplateTool;
import com.h3c.solutionhub.entity.DhcpBO;
import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.mapper.FileManagementMapper;
import com.h3c.solutionhub.mapper.NodesManagementMapper;
import com.h3c.solutionhub.service.NodesManagementService;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
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
            String token = getToken(node.getNodeHDMIP());
            node.setToken(token);

            // 2.获取管理节点mac
            String mac = getManageNodeMac(node.getNodeHDMIP(),token);
            node.setManagementMAC(mac);
        }

//        // 3.生成配置文件dhcpd.conf
//        createConfFile(dhcpBO.getDhcpIPPond(),dhcpBO.getDhcpMask(),nodes);
//
//        // 4.创建子目录（/var/www/html/UUID/）执行mount
//        execLinuxCommand(mountShell);
//
//        // 5.生成配置文件 grub.cfg-宿主机IP16进制
//        createGrubConfFile(productType,productVersion);

        for(NodeBo node:nodes) {
            // 6.PXE模式执行
            startPXE(node.getNodeHDMIP(),node.getToken());

            // 修改节点状态


            // 7.重启
            reboot(node.getNodeHDMIP(),node.getToken());
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


    private String getToken(String nodeHDMIP) {
        String url = "https://"+nodeHDMIP+"/redfish/v1/SessionService/Sessions";

        HashMap<String, Object> map = new HashMap<>();
        map.put("UserName","admin");
        map.put("Password","Password@_");

//        ResponseEntity responseEntity =restTemplateTool.sendHttps(url,map, HttpMethod.POST,null);
        HttpResponse response = new HttpClientUtil().sendHttpsPost(url,map,"");
        Header[] headers = response.getAllHeaders();
        System.out.println("响应状态为:" + response.getStatusLine());
        for(Header header:headers) {
            if(header.getName().equals("X-Auth-Token")) {
                System.out.println(header);
                return header.getValue();

            }
        }
        System.out.println("为获取到token");
        return null;
    }

    private String getManageNodeMac(String nodeMangeIP,String token) {
        String url = "https://"+nodeMangeIP+"/redfish/v1/Chassis/1/NetworkAdapters/PCIeSlot1/NetworkPorts/1";

//        ResponseEntity responseEntity =restTemplateTool.sendHttps(url,null, HttpMethod.GET,token);
        HttpResponse response = new HttpClientUtil().sendHttpsGet(url,null,token);

        HttpEntity httpEntity = response.getEntity();

        System.out.println("响应状态为:" + response.getStatusLine());
        String string = null;
        try {
            string = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String mac = string.substring(string.indexOf("[\"")+2,string.lastIndexOf("\"]"));
        System.out.println("MAC:"+mac);

        return mac;
    }

    private void startPXE(String nodeMangeIP,String token) {
        String url = "https://"+nodeMangeIP+"/redfish/v1/Systems/1";

        HashMap<String, Object> map = new HashMap<>();
        map.put("AssetTag","solutionhub");
        map.put("HostName","solutionhub");
        HashMap<String, Object> childMap = new HashMap<>();
        childMap.put("BootSourceOverrideMode","UEFI");
        childMap.put("BootSourceOverrideTarget","Pxe");
        childMap.put("BootSourceOverrideEnabled","Once");

        map.put("Boot",childMap);

        HttpResponse response = new HttpClientUtil().sendHttpsPatch2(url,map,token);
        System.out.println("reboot 响应状态为:" + response.getStatusLine());
    }

    private void reboot(String nodeMangeIP,String token) {
        String url = "https://"+nodeMangeIP+"/redfish/v1/Systems/1/Actions/ComputerSystem.Reset";

        HashMap<String, Object> map = new HashMap<>();
        map.put("ResetType","ForceRestart");

        HttpResponse response = new HttpClientUtil().sendHttpsPost(url,map,token);
        System.out.println("reboot 响应状态为:" + response.getStatusLine());

//        restTemplateTool.sendHttps(url,map,HttpMethod.PATCH,token);
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
