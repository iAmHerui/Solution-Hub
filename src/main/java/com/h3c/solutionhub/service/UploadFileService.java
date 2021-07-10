package com.h3c.solutionhub.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UploadFileService {

    Map uploadFile(MultipartFile file, HttpServletRequest request);
}
