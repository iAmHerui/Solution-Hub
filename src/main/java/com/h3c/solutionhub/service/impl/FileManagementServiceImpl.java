package com.h3c.solutionhub.service.impl;

import com.h3c.solutionhub.common.JsonResult;
import com.h3c.solutionhub.entity.FileBO;
import com.h3c.solutionhub.mapper.FileManagementMapper;
import com.h3c.solutionhub.service.FileManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileManagementServiceImpl implements FileManagementService {

    @Autowired
    private FileManagementMapper fileManagementMapper;

    @Override
    public Boolean insertFileInfo(FileBO fileBO) {

        return fileManagementMapper.insertFileInfo(
                fileBO.getFileType(),
                fileBO.getProductType(),
                fileBO.getFileName(),
                fileBO.getProductVersion(),
                fileBO.getFileSize());

    }

    @Override
    public Boolean deleteFileInfo(String fileName) {
        // 删除文件

        // 删除文件所在数据库数据
        return fileManagementMapper.deleteFileInfo(fileName);

    }
}
