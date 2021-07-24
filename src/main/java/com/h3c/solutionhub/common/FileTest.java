package com.h3c.solutionhub.common;

import org.junit.Test;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class FileTest {

    @Test
    public void fileTest() throws Exception {
        String filePath = "D:/var/"+"www/html/E0701/ks";
        File destTempFile = new File(filePath,"ks-auto.cfg");
        System.out.println(destTempFile.getParentFile());
        destTempFile.getParentFile().mkdir();
        System.out.println(destTempFile.getAbsoluteFile());
        System.out.println(destTempFile.getCanonicalFile());

    }

    @Test
    public void jsonTest() throws Exception {
        String test = "{@odata.context=/redfish/v1/$metadata#NetworkPort.NetworkPort, " +
                "@odata.etag=W/\"1626315173\", " +
                "@odata.id=/redfish/v1/Chassis/1/NetworkAdapters/PCIeSlot1/NetworkPorts/1, " +
                "@odata.type=#NetworkPort.v1_1_0.NetworkPort, " +
                "AssociatedNetworkAddresses=[54:2B:DE:0B:F1:BC], " +
                "Id=1, " +
                "LinkStatus=N/A, " +
                "Name=1, " +
                "Oem={Public={BDF=0000:5E:00.0, PortType=OpticalPort}}, " +
                "PhysicalPortNumber=1}";
        String mac = test.substring(test.indexOf("[")+1,test.lastIndexOf("]"));
        System.out.println(mac);
//        JSONObject resultJson = JSON.parseObject(test);
//        System.out.println(resultJson.getJSONArray("AssociatedNetworkAddresses").get(0).toString());
    }

    @Test
    public void test() {

            InetAddress ip4 = null;
            try {
                ip4 = Inet4Address.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            String ipString = ip4.getHostAddress();
            System.out.println(ipString);

            String test = "210.0.12.25";

            String[] ip=test.split("\\.");
            StringBuffer sb=new StringBuffer();
            for (String str : ip) {
                if(str.equals("0")) {
                    sb.append("00");
                }
                sb.append(Integer.toHexString(Integer.parseInt(str)).toUpperCase());
            }
            System.out.println(sb);
    }

    @Test
    public void fileTest2() throws IOException {
        String filePath = "D:/311/41/51/6/test.txt";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Test
    public void hostIPTest() {
        InetAddress ip4 = null;
        try {
            ip4 = Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println(ip4.getHostAddress());
    }

    @Test
    public void hostIPTest2() {
            String ip = "";
            try {
                Enumeration<?> enumeration = NetworkInterface.getNetworkInterfaces();
                while (enumeration.hasMoreElements()) {
                    NetworkInterface ni = (NetworkInterface) enumeration.nextElement();
                    if (!ni.getName().equals("eth0")) {
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
            System.out.println(ip);
    }

    @Test
    public void hostIPTest3() throws SocketException {

        Enumeration en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = (NetworkInterface) en.nextElement();
            for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
                InetAddress addr = (InetAddress) en2.nextElement();
                if (!addr.isLoopbackAddress()) {
                    if (addr instanceof Inet4Address) {
                        if (false) {
                            continue;
                        }
                        System.out.println(addr.toString());
                    }
                    if (addr instanceof Inet6Address) {
                        if (true) {
                            continue;
                        }
                        System.out.println(addr.toString());
                    }
                }
            }
        }
    }

    @Test
    public void strReplace() throws Exception {
        //原有的内容
        String srcStr = "cdrom";
        //要替换的内容
        String replaceStr = "url --url http://210.0.0.233/E0710/H3C_CAS-E0710-centos-x86_64/";
        // 读
        File file = new File("C:\\Users\\h14049\\Desktop\\Solution Hub\\ks-auto.cfg");
        FileReader in = new FileReader(file);
        BufferedReader bufIn = new BufferedReader(in);
        // 内存流, 作为临时流
        CharArrayWriter tempStream = new CharArrayWriter();
        // 替换
        String line = null;
        while ( (line = bufIn.readLine()) != null) {
            // 替换每行中, 符合条件的字符串
            line = line.replaceAll(srcStr, replaceStr);
            // 将该行写入内存
            tempStream.write(line);
            // 添加换行符
            tempStream.append(System.getProperty("line.separator"));
        }
        // 关闭 输入流
        bufIn.close();
        // 将内存中的流 写入 文件
        FileWriter out = new FileWriter(file);
        tempStream.writeTo(out);
        out.close();
    }

}
