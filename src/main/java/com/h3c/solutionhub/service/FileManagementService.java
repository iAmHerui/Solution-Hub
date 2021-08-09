package com.h3c.solutionhub.service;

import com.h3c.solutionhub.entity.FileBO;

import java.util.List;

public interface FileManagementService {

    Boolean isFileExist(String fileName,String productVersion);

    List<FileBO> getFileList();

    Boolean insertFileInfo(FileBO fileBO);

    Boolean deleteFileInfo(String fileName);

    Boolean mergeFile(String guid, FileBO fileBO);

    List<String> getAllProductType();

    List<String> getVersionByType();
}
