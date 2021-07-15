package com.h3c.solutionhub.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.File;

public class FileTest {

    public static void main(String[] args) {
        try {
            //新建文件字符替换工具类对象
            StrReplace strReplace = new StrReplace();
            //设置文件路径
            strReplace.setFilePath("E:/java");

            //可选：设置编码方式，默认为UTF-8，对某些文件若不设置编码方式可能会到这中文乱码
            strReplace.setEncoding("UTF-8");
            //可选：设置keyStr，文件名包含该字符串的文件才会被选中
            strReplace.setKeyStr(".vm");
            //可选：设置过滤词，文件名包含该字符串的文件将不会被选择，优先级高于keyStr
            strReplace.setFilterStr("副本");

            strReplace.processReplace("数量(手)", "手数");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void ReplaceTest() {
        //新建文件字符替换工具类对象
        StrReplace strReplace = new StrReplace();

        strReplace.replaceStr(new File("D:/dhcpd.conf"),"default-lease-time","herui");
    }

    @Test
    public void suffix() {
        String isoName = "CenOS.iso";
        System.out.println(isoName.substring(0,isoName.lastIndexOf(".")));
    }

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

}
