package com.h3c.solutionhub.service.impl;

import com.h3c.solutionhub.common.AsyncUtil;
import com.h3c.solutionhub.common.CommandUtil;
import com.h3c.solutionhub.common.HttpsClientUtil;
import com.h3c.solutionhub.entity.DhcpBO;
import com.h3c.solutionhub.entity.NodeBo;
import com.h3c.solutionhub.mapper.FileManagementMapper;
import com.h3c.solutionhub.mapper.NodesManagementMapper;
import com.h3c.solutionhub.service.NodesManagementService;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NodesManagementServiceImpl implements NodesManagementService {

    private static final Logger log = LoggerFactory.getLogger(NodesManagementService.class);

    @Autowired
    private NodesManagementMapper nodesManagementMapper;

    @Autowired
    private FileManagementMapper fileManagementMapper;

    @Autowired
    private AsyncUtil asyncUtil;

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

//    @Override
//    public Boolean deployNode(
//            String productType,
//            String productVersion,
//            List<NodeBo> nodes) {
//
//        // 获取当前DHCP地址
//        DhcpBO dhcpBO = nodesManagementMapper.selectDHCPInfo();
//        log.info("DHCP info SUCCESS");
//
//        for(NodeBo node:nodes) {
//            log.info("当前nodeName: "+node.getNodeName()+" ,HDMIp: "+node.getNodeHDMIP());
//            // 1.获取token
//            String token = getToken(node.getNodeHDMIP());
//            if(token==null) {
//                log.info("无法获取 "+node.getNodeName()+" token，部署失败");
//                return false;
//                // TODO 一期暂不支持批量部署
//            }
//            log.info("当前nodeName: "+node.getNodeName()+" ,token: "+token);
//            node.setToken(token);
//
//            // 2.获取管理节点mac
//            String mac = getManageNodeMac(node.getNodeHDMIP(),token);
//            log.info("当前nodeName: "+node.getNodeName()+" ,mac: "+mac);
//            node.setManagementMAC(mac);
//
//        }
//
//        // 3.生成配置文件dhcpd.conf
//        createConfFile(dhcpBO.getDhcpIPPond(),dhcpBO.getDhcpMask(),nodes);
//        log.info("dhcpd.conf 文件已生成");
//
//        // 4.执行dhcp restart
//        execDHCPCommand();
//
//        // 4.执行mount
//        String srcDir = execLinuxCommand(productType, productVersion);
//        log.info("mount 已执行,mount dir = "+srcDir);
//
//        // 5.将mount后的文件，拷贝到临时目录
////        copy(srcDir,"/var/nfs/mountCopy");
//        String copyCommand = "cp -r "+srcDir+" "+"/var/nfs/mountCopy";
//        log.info("copyCommand: "+copyCommand);
//        Boolean result = execLinuxCommand(copyCommand);
//        log.info(srcDir+" 目录下所有文件,已拷贝到 "+"/var/nfs/mountCopy "+result);
//
//        for(NodeBo node:nodes) {
//
//            // 5.生成该节点的ks-auto.cfg文件,并进行替换相关文本
//            // 5.1 生成Node ks-auto.cfg定制文件
//            String sourceCfgPath = tempFilePath+"nfs/ks/"+productVersion+"/ks-auto.cfg";
//            String destFileName = "ks-auto-"+to16(node.getManagementIP())+".cfg";
//            String desCfgPath = tempFilePath+"nfs/ks/"+productVersion+"/"+destFileName;
//            File sourceFile = new File(sourceCfgPath);
//            File desFile = new File(desCfgPath);
//            try {
//                // TODO是否需要判断desFile已存在？
//                Files.copy(sourceFile.toPath(),desFile.toPath());
//                log.info("copy ks-auto.cfg success");
//            } catch (IOException e) {
//                log.info("copy ks-auto.cfg failure");
//                e.printStackTrace();
//                return false;
//            }
//
//            // 5.2 修改Node 定制文件,替换相关文本
//            modifyDesFile(node,desCfgPath,productType);
//            log.info("modify new ks-auto.cfg success");
//
//            // 6.生成配置文件 grub.cfg-nodeManageIP16进制
//            createGrubConfFile(productType,productVersion,node.getManagementIP(),destFileName);
//            log.info("grub.cfg 文件已生成");
//
//            // 7.PXE模式执行
//            startPXE(node.getNodeHDMIP(),node.getToken());
//            log.info("当前nodeName: "+node.getNodeName()+" ,PXE配置下发成功");
//
//            // 修改节点状态
//            nodesManagementMapper.updateNodeStatus(node.getNodeId());
//
//            // 8.重启
//            reboot(node.getNodeHDMIP(),node.getToken());
//            log.info("当前nodeName: "+node.getNodeName()+" ,已强制重启");
//        }
//        return true;
//    }

    @Override
    public Boolean deploySingleNode(NodeBo node,String productVersion) {

        log.info("---------- 当前部署节点名称: "+node.getNodeName()+" ,BEGIN ----------");

        String productType = node.getNodeType();

        DhcpBO dhcpBO = nodesManagementMapper.selectDHCPInfo();
        log.info("---------- 获取当前DHCP地址,SUCCESS ----------");

        String token = getToken(node.getNodeHDMIP());
        if(token==null) {
            log.error("---------- 无法获取节点token,"+node.getNodeName()+" 部署失败 ----------");
            return false;
        }
        log.info("---------- 获取当前token,SUCCESS.当前节点: "+node.getNodeName()+",token:"+token+"----------");
        node.setToken(token);

        String mac = getManageNodeMac(node.getNodeHDMIP(),token);
        log.info("---------- 获取当前mac,SUCCESS.当前节点: "+node.getNodeName()+",mac:"+mac+"----------");
        node.setManagementMAC(mac);

        createConfFile(dhcpBO.getDhcpIPPond(),dhcpBO.getDhcpMask(),node);
        log.info("---------- 节点信息添加到dhcpd.conf,SUCCESS ----------");

        execDHCPCommand();
        log.error("---------- DHCP restart,SUCCESS ----------");

        String srcDir = execLinuxCommand(productType, productVersion);
        log.info("---------- mount iso,SUCCESS.mount dir = "+srcDir+" ----------");

//        String copyCommand = "cp -r "+srcDir+" "+"/var/nfs/mountCopy";
        String copyCommand = "cp -ru "+"/var/nfs/mountpath/"+productVersion+"/*"+" "+srcDir;
        log.info("---------- copy command: "+copyCommand+" ----------");

        List<String> commandArr = new ArrayList<>();
        commandArr.add("/bin/sh");
        commandArr.add("-c");
        commandArr.add(copyCommand);

        try {
            CommandUtil.run(commandArr.toArray(new String[commandArr.size()]));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Boolean result = execLinuxCommand(copyCommand);
        log.info("---------- copy command,SUCCESS.已拷贝到 /var/nfs/mountpath ----------");

        // 5.生成该节点的ks-auto.cfg文件,并进行替换相关文本
        // 5.1 生成Node ks-auto.cfg定制文件
        String sourceCfgPath = tempFilePath+"nfs/ks/"+productVersion+"/ks-auto.cfg";
        String destFileName = "ks-auto-"+to16(node.getManagementIP())+".cfg";
        String desCfgPath = tempFilePath+"nfs/ks/"+productVersion+"/"+destFileName;
        File sourceFile = new File(sourceCfgPath);
        File desFile = new File(desCfgPath);
        try {
            // TODO 是否需要判断desFile已存在？
            Files.copy(sourceFile.toPath(),desFile.toPath());
            log.info("---------- create new ks-auto.cfg,SUCCESS ----------");
        } catch (IOException e) {
            log.error("---------- create new ks-auto.cfg,FAILURE ----------");
            e.printStackTrace();
            return false;
        }

        // 5.2 修改Node 定制文件,替换相关文本
        modifyDesFile(node,desCfgPath,productType);
        log.info("---------- modify new ks-auto.cfg,SUCCESS ----------");

        // 6.生成配置文件 grub.cfg-nodeManageIP16进制
        createGrubConfFile(productType,productVersion,node.getManagementIP(),destFileName);
        log.info("---------- create new grub.cfg,SUCCESS ----------");

        // 7.PXE模式执行
        startPXE(node.getNodeHDMIP(),node.getToken());

        // 修改节点状态
        nodesManagementMapper.updateNodeStatus(node.getNodeId());
        log.info("---------- 节点状态已修改:占用,SUCCESS.当前节点: "+node.getNodeName()+" ----------");

        // 8.重启
        reboot(node.getNodeHDMIP(),node.getToken());

        log.info("---------- 当前部署节点名称: "+node.getNodeName()+" ,END ----------");
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

        HttpResponse response = new HttpsClientUtil().sendHttpsPost(url,map,"");
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
        String url = "";
//        if(nodeMangeIP.equals("210.0.7.210")) {
//            url = "https://"+nodeMangeIP+"/redfish/v1/Chassis/1/NetworkAdapters/PCIeSlot1/NetworkPorts/1";
//        } else {
        url = "https://" + nodeMangeIP + "/redfish/v1/Chassis/1/NetworkAdapters/mLOM/NetworkPorts/1";
//        }
//        ResponseEntity responseEntity =restTemplateTool.sendHttps(url,null, HttpMethod.GET,token);
        HttpResponse response = new HttpsClientUtil().sendHttpsGet(url,null,token);

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

        HttpResponse response = new HttpsClientUtil().sendHttpsPatch2(url,map,token);
        log.info("---------- startPXE,SUCCESS.响应状态: "+response.getStatusLine()+"----------");
    }

    private void reboot(String nodeMangeIP,String token) {
        String url = "https://"+nodeMangeIP+"/redfish/v1/Systems/1/Actions/ComputerSystem.Reset";

        HashMap<String, Object> map = new HashMap<>();
        map.put("ResetType","ForceRestart");

        HttpResponse response = new HttpsClientUtil().sendHttpsPost(url,map,token);
        log.info("---------- reboot,SUCCESS.响应状态: "+response.getStatusLine()+"----------");

    }

    /**
     * 生成配置文件dhcpd.conf
     */
    private void createConfFile(
            String dhcpIPPond,
            String dhcpMask,
            NodeBo nodeBo) {
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
        String isoName = fileManagementMapper.getISOName(productVersion);
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
                        "inst.stage2=nfs:"+hostIP()+":/var/nfs/"+productVersion+"/"+prefixName+" "+
                        "inst.ks=nfs:"+hostIP()+":/var/nfs/ks/"+productVersion+"/"+desFileName+" "+
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

        String command = "systemctl restart dhcpd";

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
        log.error("---------- DHCP restart,FAILURE ----------");
        return false;
    }

    private String execLinuxCommand(String productType, String productVersion) {

        String isoName = fileManagementMapper.getISOName(productVersion);
        String prefixName = isoName.substring(0,isoName.lastIndexOf("."));

        String filePath = "/var/nfs/"+productVersion+"/"+prefixName+"/";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String mountPath = "/var/nfs/mountpath/"+productVersion;
        File file1 = new File(mountPath);
        if (!file1.exists()) {
            file1.mkdirs();
        }

        // 执行mount shell
        String command = "mount -t auto /var/iso/"+productVersion+"/"+isoName+" "+mountPath;
        log.info("---------- mount command: "+command+" ----------");

        List<String> commandArr = new ArrayList<>();
        commandArr.add("/bin/sh");
        commandArr.add("-c");
        commandArr.add(command);

        try {
            CommandUtil.run(commandArr.toArray(new String[commandArr.size()]));
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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
                if (!ni.getName().equals(networkName)) {
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
        String sourceLine_0 = "cdrom";
//        String desLine_0 = "nfs " +
//                "--server=" + hostIP() + " " +
//                "--dir=/var/nfs/" + productType + "/H3C_CAS-E0710-centos-x86_64/";
        String desLine_0 = "";
        String sourceLine_1 = "network  --bootproto=dhcp --onboot=off --ipv6=auto --no-activate";
        String desLine_1 = "network " +
                "--device=eth0" +" " +
                "--bootproto=static " +
                "--ip=" + node.getManagementIP() +" " +
                "--netmask=" + node.getManagementMask() +" " +
                "--gateway=" + node.getManagementGateway() +" " +
                "--onboot=yes " +
                "--hostname=" + node.getNodeName();
        String sourceLine_2 = "network  --hostname=cvknode";
        String desLine_2 = "";

        strReplace(desFilePath,sourceLine_0,desLine_0);
        strReplace(desFilePath,sourceLine_1,desLine_1);
        strReplace(desFilePath,sourceLine_2,desLine_2);

        // 如果是CVM,不需要改动。如果是CVK,则需要改动
        if(productType.equals("CAS_CVK")) {
            String sourceLine_3 = "virtualization-host-environment-cvm";
            String desLine_3 = "virtualization-host-environment";
            strReplace(desFilePath,sourceLine_3,desLine_3);
        }
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

    private static void copy(String src, String des) {
        File file1=new File(src);
        File[] fs=file1.listFiles();
        File file2=new File(des);
        if(!file2.exists()){
            file2.mkdirs();
        }
        for (File f : fs) {
            if(f.isFile()){
                fileCopy(f.getPath(),des+"/"+f.getName()); //调用文件拷贝的方法
            }else if(f.isDirectory()){
                copy(f.getPath(),des+"/"+f.getName());
            }
        }

    }

    /**
     * 文件拷贝的方法
     */
    private static void fileCopy(String src, String des) {

        BufferedReader br=null;
        PrintStream ps=null;

        try {
            br=new BufferedReader(new InputStreamReader(new FileInputStream(src)));
            ps=new PrintStream(new FileOutputStream(des));
            String s=null;
            while((s=br.readLine())!=null){
                ps.println(s);
                ps.flush();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(br!=null)  br.close();
                if(ps!=null)  ps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Boolean execLinuxCommand(String command) {

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
}
