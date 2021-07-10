package com.h3c.solutionhub.service;

import com.h3c.solutionhub.entity.FileBO;

public interface FileManagementService {

    Boolean insertFileInfo(FileBO fileBO);

    Boolean deleteFileInfo(String fileName);
}
