package com.h3c.solutionhub.mapper;

import com.h3c.solutionhub.entity.FileBO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileManagementMapper {

    List<FileBO> getFileList();

    Boolean insertFileInfo(@Param("fileType") String fileType,
                           @Param("productType") String productType,
                           @Param("fileName") String fileName,
                           @Param("productVersion") String productVersion,
                           @Param("fileSize") int fileSize,
                           @Param("filePath") String filePath);

    Boolean deleteFileInfo(@Param("fileName") String fileName);

    String selectFileName(@Param("fileName") int fileId);

    String selectFilePath(@Param("fileName") String fileName);

    String getISOName(@Param("productVersion") String productVersion);

    List<String> selectAllProductType();

    List<String> selectVersion();
}
