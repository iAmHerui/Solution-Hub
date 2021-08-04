package com.h3c.solutionhub.service.impl;

import com.h3c.solutionhub.common.HttpClientUtil;
import com.h3c.solutionhub.entity.DhcpBO;
import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.mapper.FileManagementMapper;
import com.h3c.solutionhub.mapper.NodesManagementMapper;
import com.h3c.solutionhub.service.NodesManagementService;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

@Service
public class NodesManagementServiceImpl implements NodesManagementService {

    private static final Logger log = LoggerFactory.getLogger(NodesManagementService.class);

    @Autowired
    private NodesManagementMapper nodesManagementMapper;

    @Autowired
    private FileManagementMapper fileManagementMapper;

    @Value("${tempFilePath}")
    private String tempFilePath;

    @Value("${dhcpFilePath}")
    private String dhcpFilePath;

    @Value("${grubFilePath}")
    private String grubFilePath;

    @Value("${networkName}")
    private String networkName;

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
        log.info("DHCP info SUCCESS");

        for(NodeBo node:nodes) {
            log.info("当前nodeName: "+node.getNodeName()+" ,HDMIp: "+node.getNodeHDMIP());
            // 1.获取token
            String token = getToken(node.getNodeHDMIP());
            if(token==null) {
                log.info("无法获取 "+node.getNodeName()+" token，部署失败");
                return false;
                // TODO 一期暂不支持批量部署
            }
            log.info("当前nodeName: "+node.getNodeName()+" ,token: "+token);
            node.setToken(token);

            // 2.获取管理节点mac
            String mac = getManageNodeMac(node.getNodeHDMIP(),token);
            log.info("当前nodeName: "+node.getNodeName()+" ,mac: "+mac);
            node.setManagementMAC(mac);

        }

        // 3.生成配置文件dhcpd.conf
        createConfFile(dhcpBO.getDhcpIPPond(),dhcpBO.getDhcpMask(),nodes);
        log.info("dhcpd.conf 文件已生成");

        // 4.执行dhcp restart
        execDHCPCommand();

        // 4.执行mount
        Boolean result = execLinuxCommand(productType, productVersion);
        log.info("mount 执行 "+result);

        for(NodeBo node:nodes) {

            // 5.生成该节点的ks-auto.cfg文件,并进行替换相关文本
            // 5.1 生成Node ks-auto.cfg定制文件
            String sourceCfgPath = tempFilePath+"nfs/ks/"+productVersion+"/ks-auto.cfg";
            String destFileName = "ks-auto-"+to16(node.getManagementIP())+".cfg";
            String desCfgPath = tempFilePath+"nfs/ks/"+productVersion+"/"+destFileName;
            File sourceFile = new File(sourceCfgPath);
            File desFile = new File(desCfgPath);
            try {
                Files.copy(sourceFile.toPath(),desFile.toPath());
            } catch (IOException e) {
                log.info("copy ks-auto.cfg failure");
                e.printStackTrace();
                return false;
            }

            // 5.2 修改Node 定制文件,替换相关文本
            modifyDesFile(node,desCfgPath,productType);

            // 6.生成配置文件 grub.cfg-nodeManageIP16进制
            createGrubConfFile(productType,productVersion,node.getManagementIP(),destFileName);
            log.info("grub.cfg 文件已生成");

            // 7.PXE模式执行
            startPXE(node.getNodeHDMIP(),node.getToken());
            log.info("当前nodeName: "+node.getNodeName()+" ,PXE配置下发成功");

            // 修改节点状态
            nodesManagementMapper.updateNodeStatus(node.getNodeId());

            // 8.重启
            reboot(node.getNodeHDMIP(),node.getToken());
            log.info("当前nodeName: "+node.getNodeName()+" ,已强制重启");
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

    @Override
    public Boolean isNodeExist(String nodeName) {
        // 查询 nodeName 重名个数
        int count = nodesManagementMapper.isNodeExist(nodeName);
        if(count>0) {
            return false;
        } else {
            return true;
        }
    }


    private String getToken(String nodeHDMIP) {
        String url = "https://"+nodeHDMIP+"/redfish/v1/SessionService/Sessions";

        HashMap<String, Object> map = new HashMap<>();
        map.put("UserName","admin");
        map.put("Password","Password@_");

        HttpResponse response = new HttpClientUtil().sendHttpsPost(url,map,"");
        if(response!=null) {
            log.info("响应状态为:" + response.getStatusLine());
            Header[] headers = response.getAllHeaders();
            for(Header header:headers) {
                if(header.getName().equals("X-Auth-Token")) {
                    log.info("获取token成功");
                    return header.getValue();
                }
            }
        }
        log.info("获取token失败");
        return null;
    }

    private String getManageNodeMac(String nodeMangeIP,String token) {
        String url = "https://"+nodeMangeIP+"/redfish/v1/Chassis/1/NetworkAdapters/mLOM/NetworkPorts/1";

//        ResponseEntity responseEntity =restTemplateTool.sendHttps(url,null, HttpMethod.GET,token);
        HttpResponse response = new HttpClientUtil().sendHttpsGet(url,null,token);

        HttpEntity httpEntity = response.getEntity();

        log.info("响应状态为:" + response.getStatusLine());
        String string = "";
        try {
            string = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            log.info("i am here");
            e.printStackTrace();
        }
        String mac = string.substring(string.indexOf("[\"")+2,string.lastIndexOf("\"]"));
        System.out.println("MAC: "+mac);
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
                            "filename \"BOOTX64.EFI\";\n" +
                            "next-server " + hostIP() + ";\n" +
                            "}\n";
        }
        for(NodeBo nodeBo:nodeList) {
            if(!nodesManagementMapper.selectNodeStatus(nodeBo.getNodeName()).equals("占用")) {
                log.info("nodeName: "+nodeBo.getNodeName()+" 该节点未占用，添加DHCP配置");
                String mac = nodeBo.getManagementMAC();
                String ip = nodeBo.getManagementIP();
                String confNodeInfo =
                        "host " + nodeBo.getNodeName() + " {\n" +
                                "    hardware ethernet " + mac + ";\n" +
                                "    fixed-address " + ip + ";\n" +
                                "}\n";
                confInfo = confInfo + confNodeInfo;
            }
        }
        createFileForDHCPConf(confInfo,filePath);
    }

    private Boolean createNodeKsCfg() {
        return true;
    }

    /**
     * 生成配置文件grub.cfg
     */
    private void createGrubConfFile(String productType, String productVersion,String nodeManageIp,String desFileName) {

        //获取文件名，不要后缀
        String isoName = fileManagementMapper.getISOName(productType,productVersion);
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
                        "       linuxefi /images/pxeboot/vmlinuz "+
                        "inst.stage2=nfs:"+hostIP()+"/var/nfs/"+productVersion+"/"+prefixName+" "+
                        "inst.ks=nfs:"+hostIP()+"/var/nfs/ks/"+productVersion+"/"+desFileName+" "+
                        "net.ifnames=0 "+
                        "biosdevname=0 "+
                        "quiet\n"+
                        "       initrdefi /images/pxeboot/initrd.img\n"+
                        "}\n";

        createFileForGrubConf(fileInfo,grubFilePath+"-"+to16(nodeManageIp));
    }

    private void createFileForDHCPConf(String fileInfo,String filePath) {
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

    private void createFileForGrubConf(String fileInfo,String filePath) {
        try {
            File file = new File(filePath);
            // 如果grub存在，将不会写入任何数据
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();

                OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(fileInfo);
                bw.flush();
                bw.close();
                fw.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean execDHCPCommand() {

        // 执行mount shell
        String command = "systemctl restart dhcpd";
        log.info("DHCP command: "+command);

        Runtime run = Runtime.getRuntime();
        Process process = null;

        try {
            process = run.exec(command);
            process.waitFor();
            process.destroy();
            log.info("DHCP restart SUCCESS");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("DHCP restart FAILURE");
        return false;
    }

    private Boolean execLinuxCommand(String productType, String productVersion) {

        String isoName = fileManagementMapper.getISOName(productType,productVersion);
        String prefixName = isoName.substring(0,isoName.lastIndexOf("."));
        //创建 /var/www/html/version/iso名字前缀/ 目录
        String filePath = "/var/nfs/"+productVersion+"/"+prefixName;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        // 执行mount shell
        String command = "mount -t auto /var/iso/"+productVersion+"/"+isoName+" /var/nfs/"+productVersion+"/"+prefixName;
        log.info("mount command: "+command);

        Runtime run = Runtime.getRuntime();
        Process process = null;

        try {
            process = run.exec(command);
            process.waitFor();
            process.destroy();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String to16(String ipString) {
            String[] ip=ipString.split("\\.");
            StringBuffer sb=new StringBuffer();
            for (String str : ip) {
                if(str.equals("0")) {
                    sb.append("00");
                }
                sb.append(Integer.toHexString(Integer.parseInt(str)).toUpperCase());
            }
            return sb.toString();
    }

//    private String hostIP() {
//        InetAddress ip4 = null;
//        try {
//            ip4 = Inet4Address.getLocalHost();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        return ip4.getHostAddress();
//    }

    private String hostIP() {
        String ip = "";
        try {
            Enumeration<?> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) enumeration.nextElement();
                if (!ni.getName().equals("networkName")) {
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
        log.info("当前 IP 所属网卡: "+networkName);
        return ip;
    }

//    @Test
//    public void test() {
//        NodeBo nodeBo = new NodeBo();
//        nodeBo.setManagementIP("1.1.1.1");
//        nodeBo.setManagementGateway("1.1.1.0");
//        nodeBo.setManagementMask("255.255.255.0");
//        nodeBo.setNodeName("node_test");
//
//        modifyDesFile(nodeBo,"D:\\test\\ks-auto2.cfg","CAS_CVK");
//    }

    private void modifyDesFile(NodeBo node,String desFilePath,String productType) {
        String sourceLine_1 = "network  --bootproto=dhcp --onboot=off --ipv6=auto --no-activate";
        String desLine_1 = "network " +
                "--device=" + networkName +" " +
                "--bootproto=static " +
                "--ip=" + node.getManagementIP() +" " +
                "--netmask=255.255.224.0" +" " +
                "--gateway=210.0.0.254" +" " +
//                "--netmask=" + node.getManagementMask() +" " +
//                "--gateway=" + node.getManagementGateway() +" " +
                "--onboot=yes " +
                "--hostname=" + node.getNodeName();
        String sourceLine_2 = "network  --hostname=cvknode";
        String desLine_2 = "";

        strReplace(desFilePath,sourceLine_1,desLine_1);
        strReplace(desFilePath,sourceLine_2,desLine_2);

        // 如果是CVM,不需要改动。如果是CVK,则需要改动
        if(productType.equals("CAS_CVK")) {
            String sourceLine_3 = "virtualization-host-environment-cvm";
            String desLine_3 = "virtualization-host-environment";
            strReplace(desFilePath,sourceLine_3,desLine_3);
        }
        log.info("ks-auto.cfg modify success!");
    }

    private void strReplace(String path,String srcStr,String replaceStr) {
        File file = new File(path);
        BufferedReader bufIn = null;
        FileWriter out = null;
        try {
            FileReader in = new FileReader(file);
            bufIn = new BufferedReader(in);
            // 内存流, 作为临时流
            CharArrayWriter tempStream = new CharArrayWriter();
            // 替换
            String line = null;
            while ((line = bufIn.readLine()) != null) {
                // 替换每行中, 符合条件的字符串
                line = line.replaceAll(srcStr, replaceStr);
                // 将该行写入内存
                tempStream.write(line);
                // 添加操作系统对应的换行符
                tempStream.append(System.getProperty("line.separator"));
            }

            // 将内存中的流 写入 文件
            out = new FileWriter(file);
            tempStream.writeTo(out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufIn.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
