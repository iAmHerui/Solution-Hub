package com.h3c.solutionhub.entity;

import lombok.Data;

@Data
public class FileBO {

    // 文件类型
    private String fileType;

    // 产品类型
    private String productType;

    // 文件名称
    private String fileName;

    // 文件版本
    private String productVersion;

    // 文件大小(KB)
    private int fileSize;

    // 文件路径
    private String filePath;
}
