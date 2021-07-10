package com.h3c.solutionhub.service.impl;

import com.h3c.solutionhub.common.UploadFileTool;
import com.h3c.solutionhub.service.UploadFileService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class UploadFileServiceImpl implements UploadFileService {

    @Override
    public Map uploadFile(MultipartFile file, HttpServletRequest request) {
        return UploadFileTool.fileUpload(file,request);
    }
}
