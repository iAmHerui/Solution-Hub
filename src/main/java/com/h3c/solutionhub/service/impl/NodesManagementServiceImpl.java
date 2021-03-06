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
import org.junit.Test;
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
//        // ????????????DHCP??????
//        DhcpBO dhcpBO = nodesManagementMapper.selectDHCPInfo();
//        log.info("DHCP info SUCCESS");
//
//        for(NodeBo node:nodes) {
//            log.info("??????nodeName: "+node.getNodeName()+" ,HDMIp: "+node.getNodeHDMIP());
//            // 1.??????token
//            String token = getToken(node.getNodeHDMIP());
//            if(token==null) {
//                log.info("???????????? "+node.getNodeName()+" token???????????????");
//                return false;
//                // TODO ??????????????????????????????
//            }
//            log.info("??????nodeName: "+node.getNodeName()+" ,token: "+token);
//            node.setToken(token);
//
//            // 2.??????????????????mac
//            String mac = getManageNodeMac(node.getNodeHDMIP(),token);
//            log.info("??????nodeName: "+node.getNodeName()+" ,mac: "+mac);
//            node.setManagementMAC(mac);
//
//        }
//
//        // 3.??????????????????dhcpd.conf
//        createConfFile(dhcpBO.getDhcpIPPond(),dhcpBO.getDhcpMask(),nodes);
//        log.info("dhcpd.conf ???????????????");
//
//        // 4.??????dhcp restart
//        execDHCPCommand();
//
//        // 4.??????mount
//        String srcDir = execLinuxCommand(productType, productVersion);
//        log.info("mount ?????????,mount dir = "+srcDir);
//
//        // 5.???mount????????????????????????????????????
////        copy(srcDir,"/var/nfs/mountCopy");
//        String copyCommand = "cp -r "+srcDir+" "+"/var/nfs/mountCopy";
//        log.info("copyCommand: "+copyCommand);
//        Boolean result = execLinuxCommand(copyCommand);
//        log.info(srcDir+" ?????????????????????,???????????? "+"/var/nfs/mountCopy "+result);
//
//        for(NodeBo node:nodes) {
//
//            // 5.??????????????????ks-auto.cfg??????,???????????????????????????
//            // 5.1 ??????Node ks-auto.cfg????????????
//            String sourceCfgPath = tempFilePath+"nfs/ks/"+productVersion+"/ks-auto.cfg";
//            String destFileName = "ks-auto-"+to16(node.getManagementIP())+".cfg";
//            String desCfgPath = tempFilePath+"nfs/ks/"+productVersion+"/"+destFileName;
//            File sourceFile = new File(sourceCfgPath);
//            File desFile = new File(desCfgPath);
//            try {
//                // TODO??????????????????desFile????????????
//                Files.copy(sourceFile.toPath(),desFile.toPath());
//                log.info("copy ks-auto.cfg success");
//            } catch (IOException e) {
//                log.info("copy ks-auto.cfg failure");
//                e.printStackTrace();
//                return false;
//            }
//
//            // 5.2 ??????Node ????????????,??????????????????
//            modifyDesFile(node,desCfgPath,productType);
//            log.info("modify new ks-auto.cfg success");
//
//            // 6.?????????????????? grub.cfg-nodeManageIP16??????
//            createGrubConfFile(productType,productVersion,node.getManagementIP(),destFileName);
//            log.info("grub.cfg ???????????????");
//
//            // 7.PXE????????????
//            startPXE(node.getNodeHDMIP(),node.getToken());
//            log.info("??????nodeName: "+node.getNodeName()+" ,PXE??????????????????");
//
//            // ??????????????????
//            nodesManagementMapper.updateNodeStatus(node.getNodeId());
//
//            // 8.??????
//            reboot(node.getNodeHDMIP(),node.getToken());
//            log.info("??????nodeName: "+node.getNodeName()+" ,???????????????");
//        }
//        return true;
//    }

    @Override
    public Boolean deploySingleNode(NodeBo node,String productVersion) {

        log.info("---------- ????????????????????????: "+node.getNodeName()+" ,BEGIN ----------");

        String productType = node.getNodeType();

        DhcpBO dhcpBO = nodesManagementMapper.selectDHCPInfo();
        log.info("---------- ????????????DHCP??????,SUCCESS ----------");

        String token = getToken(node.getNodeHDMIP());
        if(token==null) {
            log.error("---------- ??????????????????token,"+node.getNodeName()+" ???????????? ----------");
            return false;
        }
        log.info("---------- ????????????token,SUCCESS.????????????: "+node.getNodeName()+",token:"+token+"----------");
        node.setToken(token);

        String mac = getManageNodeMac(node.getNodeHDMIP(),token);
        log.info("---------- ????????????mac,SUCCESS.????????????: "+node.getNodeName()+",mac:"+mac+"----------");
        node.setManagementMAC(mac);

        createConfFile(dhcpBO.getDhcpIPPond(),dhcpBO.getDhcpMask(),node);
        log.info("---------- ?????????????????????dhcpd.conf,SUCCESS ----------");

        execDHCPCommand();
        log.info("---------- DHCP restart,SUCCESS ----------");

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
        log.info("---------- copy command,SUCCESS.???????????? /var/nfs/mountpath ----------");

        // 5.??????????????????ks-auto.cfg??????,???????????????????????????
        // 5.1 ??????Node ks-auto.cfg????????????
        String sourceCfgPath = tempFilePath+"nfs/ks/"+productVersion+"/ks-auto.cfg";
        String destFileName = "ks-auto-"+to16(node.getManagementIP())+".cfg";
        String desCfgPath = tempFilePath+"nfs/ks/"+productVersion+"/"+destFileName;
        File sourceFile = new File(sourceCfgPath);
        File desFile = new File(desCfgPath);
        try {
            // TODO ??????????????????desFile????????????
            Files.copy(sourceFile.toPath(),desFile.toPath());
            log.info("---------- create new ks-auto.cfg,SUCCESS ----------");
        } catch (IOException e) {
            log.error("---------- create new ks-auto.cfg,FAILURE ----------");
            e.printStackTrace();
            return false;
        }

        // 5.2 ??????Node ????????????,??????????????????
        modifyDesFile(node,desCfgPath,productType);
        log.info("---------- modify new ks-auto.cfg,SUCCESS ----------");

        // 6.?????????????????? grub.cfg-nodeManageIP16??????
        createGrubConfFile(productType,productVersion,node.getManagementIP(),destFileName);
        log.info("---------- create new grub.cfg,SUCCESS ----------");

        // 7.PXE????????????
        startPXE(node.getNodeHDMIP(),node.getToken());

        // ??????????????????
        nodesManagementMapper.updateNodeStatus(node.getNodeId());
        log.info("---------- ?????????????????????:??????,SUCCESS.????????????: "+node.getNodeName()+" ----------");

        // 8.??????
        reboot(node.getNodeHDMIP(),node.getToken());

        log.info("---------- ????????????????????????: "+node.getNodeName()+" ,END ----------");
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
        // ?????? nodeName ????????????
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
            log.info("???????????????:" + response.getStatusLine());
            Header[] headers = response.getAllHeaders();
            for(Header header:headers) {
                if(header.getName().equals("X-Auth-Token")) {
                    log.info("??????token??????");
                    return header.getValue();
                }
            }
        }
        log.info("??????token??????");
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

        log.info("???????????????:" + response.getStatusLine());
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
        log.info("---------- startPXE,SUCCESS.????????????: "+response.getStatusLine()+"----------");
    }

    private void reboot(String nodeMangeIP,String token) {
        String url = "https://"+nodeMangeIP+"/redfish/v1/Systems/1/Actions/ComputerSystem.Reset";

        HashMap<String, Object> map = new HashMap<>();
        map.put("ResetType","ForceRestart");

        HttpResponse response = new HttpsClientUtil().sendHttpsPost(url,map,token);
        log.info("---------- reboot,SUCCESS.????????????: "+response.getStatusLine()+"----------");

    }

    /**
     * ??????????????????dhcpd.conf
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
        if(!nodesManagementMapper.selectNodeStatus(nodeBo.getNodeName()).equals("??????")) {
            log.info("nodeName: "+nodeBo.getNodeName()+" ???????????????????????????DHCP??????");
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
     * ??????????????????grub.cfg
     */
    private void createGrubConfFile(String productType, String productVersion,String nodeManageIp,String desFileName) {

        //??????????????????????????????
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
            // ??????grub????????????????????????????????????
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

        // ??????mount shell
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
            String piece = Integer.toHexString(Integer.parseInt(str)).toUpperCase();

            if(piece.equals("0")) {
                sb.append("00");
            } else if(piece.length()==1) {
                sb.append("0"+piece);
            } else {
                sb.append(piece);
            }
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
        log.info("?????? IP ????????????: "+networkName);
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

        // ?????????CVM,???????????????????????????CVK,???????????????
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
            // ?????????, ???????????????
            CharArrayWriter tempStream = new CharArrayWriter();
            // ??????
            String line = null;
            while ((line = bufIn.readLine()) != null) {
                // ???????????????, ????????????????????????
                line = line.replaceAll(srcStr, replaceStr);
                // ?????????????????????
                tempStream.write(line);
                // ????????????????????????????????????
                tempStream.append(System.getProperty("line.separator"));
            }

            // ?????????????????? ?????? ??????
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
                fileCopy(f.getPath(),des+"/"+f.getName()); //???????????????????????????
            }else if(f.isDirectory()){
                copy(f.getPath(),des+"/"+f.getName());
            }
        }

    }

    /**
     * ?????????????????????
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
