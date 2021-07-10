package com.h3c.solutionhub.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;


public class StrReplace {

    /**
     * 文件或文件夹路径
     */
    private String filePath;

    /**
     * 递归读取的file对象列表
     */
    private List<File> files = new LinkedList<File>();
    /**
     * 文件编码方式
     */
    private String encoding = "UTF-8";

    /**
     * 文件名选中条件
     */
    private String keyStr = ".";

    /**
     * 文件名过滤条件
     */
    private String filterStr = "###";

    /**
     * 启动ReplaceStr
     * @param oldStr 被替换的字符串
     * @param newStr 用于替换的字符串
     */
    public void processReplace(String oldStr,String newStr){
        readFiles(filePath);
        for(int i=0;i<files.size();i++){
            if(files.get(i).exists() && files.get(i).isFile())
                replaceStr(files.get(i), oldStr, newStr);
        }
    }

    /**
     * 替换动作的实现
     * @param file 将要操作的文件对象
     * @param oldStr 被替换的字符串
     * @param newStr 用于替换的字符串
     */
    public void replaceStr(File file, String oldStr, String newStr) {
        InputStreamReader reader = null;
        StringBuffer sb= new StringBuffer("");
        String line = "";

        try {
            // TODO 增加功能：①刷新多个修改参数②将文件写到一个新的目录
            reader = new InputStreamReader(new FileInputStream(file),encoding);
            BufferedReader br = new BufferedReader(reader);
            while((line = br.readLine()) != null) {
                //System.out.println(line);
                if(line.indexOf(oldStr)>-1){
                    System.out.println(line);
                    line = line.substring(0,line.indexOf(oldStr))+newStr+line.substring(line.indexOf(oldStr)+oldStr.length());
                    System.out.println(line);
                }
                sb.append(line+"\n\t");
                //System.out.println(line);
            }
            br.close();
            reader.close();

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),encoding);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(sb.toString());
            bw.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化文件或文件夹，递归读取文件到内存
     * @param filePath
     */
    private void readFiles(String filePath) {
        if(filePath == null)
            return;
        File temp =  new File(filePath);
        System.out.println(temp.getPath());
        File[] file = null;

        if(temp.isFile()){
            files.add(temp);
            //System.out.println(temp.getPath());
        }else{
            file = temp.listFiles();
        }
        if(file == null){
            return;
        }
        for(File f : file){
            if(f.isFile()){
                String fileName = f.getName();
                //System.out.println("filename:   "+fileName);
                if(fileName.indexOf(keyStr)>=0)//判断文件名中是否存在关键词,若存在，则选中
                    if(fileName.indexOf(filterStr)==-1){//判断文件名中是否存在过滤词,若不存在，则选中
                        files.add(f);
                        //System.out.println("filePath:  "+f.getPath());
                    }
            }else if(f.isDirectory()){
                readFiles(f.getPath());
                //System.out.println(f.getPath());
            }else
                System.out.println("文件读入有误！");
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) throws Exception {
        if(filePath != null){
            this.filePath = filePath;
        }else
            throw new Exception("文件名不能为空");
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public String getFilterStr() {
        return filterStr;
    }

    public void setFilterStr(String filterStr) {
        this.filterStr = filterStr;
    }

}
