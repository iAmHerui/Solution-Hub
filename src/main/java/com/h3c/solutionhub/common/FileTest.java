package com.h3c.solutionhub.common;

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


}
