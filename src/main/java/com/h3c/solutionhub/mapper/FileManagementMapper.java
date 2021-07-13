package com.h3c.solutionhub.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileManagementMapper {

    Boolean insertFileInfo(@Param("fileType") String fileType,
                           @Param("productType") String productType,
                           @Param("fileName") String fileName,
                           @Param("productVersion") String productVersion,
                           @Param("fileSize") int fileSize,
                           @Param("filePath") String filePath);

    Boolean deleteFileInfo(@Param("fileName") String fileName);

    String selectFilePath(@Param("fileName") String fileName);

    String getISOName(@Param("productType") String productType,
                      @Param("productVersion") String productVersion);
}
