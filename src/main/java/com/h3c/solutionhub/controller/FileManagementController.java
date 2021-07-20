package com.h3c.solutionhub.controller;

import com.h3c.solutionhub.entity.FileBO;
import com.h3c.solutionhub.service.FileManagementService;
import com.h3c.solutionhub.service.UploadFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Api(value = "文件管理",tags = "文件管理")
@RestController
@CrossOrigin
@RequestMapping(value = "/fileManagement")
public class FileManagementController {

    private static final Logger log = LoggerFactory.getLogger(FileManagementController.class);

    @Autowired
    UploadFileService uploadFileService;

    @Autowired
    FileManagementService fileManagementService;

    @Value("${tempFilePath}")
    private String tempFilePath;

    /**
     * 上传文件,进行分片存储
     *
     * @param request
     * @param response
     * @param guid UUID
     * @param chunk 第几片文件
     * @param file
     * @param chunks
     */
    @CrossOrigin(origins ="*",maxAge =3600)
    @RequestMapping("/upload")
    public void bigFile(HttpServletRequest request,
                        HttpServletResponse response,
                        String guid,
                        @RequestParam(defaultValue = "0") Integer chunk,
                        MultipartFile file,
                        Integer chunks) {
        try {
            // 若返回值为true则是带文件上传的表单；返回值为false则是普通表单。
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (isMultipart) {
                // 临时目录用来存放所有分片文件
                String tempFileDir = tempFilePath + guid;
                File parentFileDir = new File(tempFileDir);
                if (!parentFileDir.exists()) {
                    parentFileDir.mkdirs();
                }
                // 分片处理时，前台会多次调用上传接口，每次都会上传文件的一部分到后台
                File tempPartFile = new File(parentFileDir, guid + "_" + chunk + ".part");
                FileUtils.copyInputStreamToFile(file.getInputStream(), tempPartFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 合并文件
     *
     * @param guid UUID
     * @param fileBO 文件
     * @throws Exception
     */
    @ApiOperation(value = "文件合并",notes = "文件合并")
    @CrossOrigin(origins ="*",maxAge =3600)
    @RequestMapping("/merge")
    @ResponseBody
    public Boolean mergeFile(String guid, FileBO fileBO) {
//        /** test **/
//        fileBO.setFileName("何锐-简历.docx");
//        fileBO.setProductVersion("E0701");
//        /** test **/
        return fileManagementService.mergeFile(guid,fileBO);
    }

    @ApiOperation(value = "文件删除",notes = "文件删除")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/fileDelete")
    public Boolean fileDelete(String fileName) {
        return fileManagementService.deleteFileInfo(fileName);
    }
}
