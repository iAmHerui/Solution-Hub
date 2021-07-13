package com.h3c.solutionhub.service.impl;

import com.h3c.solutionhub.entity.FileBO;
import com.h3c.solutionhub.mapper.FileManagementMapper;
import com.h3c.solutionhub.service.FileManagementService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileManagementServiceImpl implements FileManagementService {

    private static final Logger log = LoggerFactory.getLogger(FileManagementService.class);

    @Autowired
    private FileManagementMapper fileManagementMapper;

    @Value("${tempFilePath}")
    private String tempFilePath;

    @Override
    public Boolean insertFileInfo(FileBO fileBO) {

        return fileManagementMapper.insertFileInfo(
                fileBO.getFileType(),
                fileBO.getProductType(),
                fileBO.getFileName(),
                fileBO.getProductVersion(),
                fileBO.getFileSize(),
                fileBO.getFilePath());

    }

    @Override
    public Boolean deleteFileInfo(String fileName) {
        // 根据文件名查询文件所在目录
        String filePath = fileManagementMapper.selectFilePath(fileName);

        // 删除文件
        File file = new File(filePath+fileName);
        if(file.delete()) {
            // 删除文件所在数据库数据
            return fileManagementMapper.deleteFileInfo(fileName);
        }
        return false;
    }

    @Override
    public Boolean mergeFile(String guid, FileBO fileBO) {
        // 得到 destTempFile 就是最终的文件
        try {
            // 获取文件后缀
            String suffix = fileBO.getFileName().substring(fileBO.getFileName().lastIndexOf(".")+1);

            // iso和cfg要放在不同的目录下
            if(suffix.equals("iso")) {
                File parentFileDir = new File(tempFilePath);
                if(parentFileDir.isDirectory()) {
                    String filePath = tempFilePath+"/iso/"+fileBO.getProductVersion();
                    fileBO.setFilePath(filePath);
                    File destTempFile = new File(filePath,fileBO.getFileName());
                    // 创建文件
                    createFile(destTempFile,parentFileDir,guid);
                }
            } else if(suffix.equals("cfg")) {
                File parentFileDir = new File(tempFilePath);
                if(parentFileDir.isDirectory()) {
                    String filePath = tempFilePath+"/www/html/"+fileBO.getProductVersion()+"/ks";
                    fileBO.setFilePath(filePath);
                    File destTempFile = new File(filePath,fileBO.getFileName());
                    // 创建文件
                    createFile(destTempFile,parentFileDir,guid);
                }
            } else {
                // TODO 适配其他文件类型
            }

            // 录入数据库
            insertFileInfo(fileBO);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Boolean createFile(File destTempFile,File parentFileDir,String guid) throws IOException{
        // 文件是否已存在
        if (!destTempFile.exists()) {
            //先得到文件的上级目录，并创建上级目录，在创建文件,
            destTempFile.getParentFile().mkdir();

            // 创建文件，但此时没有数据
            destTempFile.createNewFile();

            // 分片文件合并
            fileMerge(parentFileDir,guid,destTempFile);

            // 删除临时目录中的分片文件
            FileUtils.deleteDirectory(parentFileDir);

            return true;
        } else {
            // 删除临时目录中的分片文件
            FileUtils.deleteDirectory(parentFileDir);
            log.error("文件已存在！");
            return false;
        }
    }

    private Boolean fileMerge(File parentFileDir,String guid,File destTempFile) throws IOException {
        // 把分片数据合入文件
        for (int i = 0; i < parentFileDir.listFiles().length; i++) {
            File partFile = new File(parentFileDir, guid + "_" + i + ".part");
            FileOutputStream destTempfos = new FileOutputStream(destTempFile, true);
            //遍历"所有分片文件"到"最终文件"中
            FileUtils.copyFile(partFile, destTempfos);
            destTempfos.close();
        }
        return true;
    }
}
