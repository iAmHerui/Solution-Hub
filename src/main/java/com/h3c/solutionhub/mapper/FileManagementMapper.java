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

    Boolean deleteFileInfo(@Param("fileId") int fileId);

    String selectFileName(@Param("fileId") int fileId);

    String selectFilePath(@Param("fileId") int fileId);

    String getISOName(@Param("productVersion") String productVersion);

    List<String> selectAllProductType();

    List<String> selectVersion();
}
