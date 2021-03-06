package com.h3c.solutionhub.common;

import org.junit.Test;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
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
        //???????????????
        String srcStr = "cdrom";
        //??????????????????
        String replaceStr = "url --url http://210.0.0.233/E0710/H3C_CAS-E0710-centos-x86_64/";
        // ???
        File file = new File("C:\\Users\\h14049\\Desktop\\Solution Hub\\ks-auto.cfg");
        FileReader in = new FileReader(file);
        BufferedReader bufIn = new BufferedReader(in);
        // ?????????, ???????????????
        CharArrayWriter tempStream = new CharArrayWriter();
        // ??????
        String line = null;
        while ( (line = bufIn.readLine()) != null) {
            // ???????????????, ????????????????????????
            line = line.replaceAll(srcStr, replaceStr);
            // ?????????????????????
            tempStream.write(line);
            // ???????????????
            tempStream.append(System.getProperty("line.separator"));
        }
        // ?????? ?????????
        bufIn.close();
        // ?????????????????? ?????? ??????
        FileWriter out = new FileWriter(file);
        tempStream.writeTo(out);
        out.close();
    }

    /** ???????????? **/
    @Test
    public void strReplace2() throws Exception {

        String filePath ="D:\\test\\test.txt";
        File file = new File(filePath);

        String filePath2 ="D:\\test\\testA2.txt";
        File file2 = new File(filePath2);

        Files.copy(file.toPath(),file2.toPath());

    }

    @Test
    public void testFile() throws Exception {
        File file = new File("D:\\test\\test1.txt");
        System.out.println(file.exists());
        System.out.println(file.isDirectory());

        File file1 = new File("D:\\test");
        System.out.println(file1.exists());
        System.out.println(file1.isDirectory());

        File file2 = new File("D:\\test","test1.txt");
        System.out.println(file2.exists());
    }

    private void FileTest(File dir) throws Exception {
        if (!dir.exists() || !dir.isDirectory()) {// ????????????????????????
            return;
        }
        String[] files = dir.list();// ??????????????????????????????????????????
        for (int i = 0; i < files.length; i++) {// ???????????????????????????????????????
            File file = new File(dir, files[i]);
            if (file.isFile()) {// ????????????
                System.out.println(dir + "\\" + file.getName());
//                fileNames.add(dir + "\\" + file.getName());// ????????????????????????
            } else {// ???????????????
                FileTest(file);
            }
        }
    }

    @Test
    public void FileTest1() throws Exception {
        String dirString = "D:\\test";
        File dir = new File(dirString);
        FileTest(dir);
    }

    @Test
    public void CopyDir() {
        copy("D:\\test","D:\\test_copy");
        System.out.println("??????????????????!");
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
                fileCopy(f.getPath(),des+"\\"+f.getName()); //???????????????????????????
            }else if(f.isDirectory()){
                copy(f.getPath(),des+"\\"+f.getName());
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

}
