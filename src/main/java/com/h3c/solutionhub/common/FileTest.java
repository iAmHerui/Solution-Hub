package com.h3c.solutionhub.common;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

}
