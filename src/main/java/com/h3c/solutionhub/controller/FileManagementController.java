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
import java.util.List;

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

    @ApiOperation(value = "判断文件是否已上传",notes = "判断文件是否已上传")
    @CrossOrigin(origins ="*",maxAge =3600)
    @GetMapping(value = "/isFileExist")
    public Boolean isFileExist(String fileName,String productVersion) {
        return fileManagementService.isFileExist(fileName,productVersion);
    }

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
    @PostMapping("/upload")
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
                log.info("第 "+chunk+" 分片已上传");
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
    @PostMapping("/merge")
    @ResponseBody
    public String mergeFile(String guid, FileBO fileBO) {
        Boolean result = fileManagementService.mergeFile(guid,fileBO);
        if(result==true) {
            return "文件上传成功";
        } else {
            return "文件上传失败";
        }
    }

    @ApiOperation(value = "文件列表",notes = "获取文件列表")
    @CrossOrigin(origins ="*",maxAge =3600)
    @GetMapping(value = "/fileInfo")
    public List<FileBO> fileInfo() {
        return fileManagementService.getFileList();
    }

    @ApiOperation(value = "文件删除",notes = "文件删除")
    @CrossOrigin(origins ="*",maxAge =3600)
    @PostMapping(value = "/fileDelete")
    public String fileDelete(int fileId) {
        Boolean result = fileManagementService.deleteFileInfo(fileId);
        if(result==true) {
            return "文件删除成功";
        } else {
            return "文件删除失败";
        }
    }

    @ApiOperation(value = "获取所有文件类型",notes = "获取所有文件类型")
    @CrossOrigin(origins ="*",maxAge =3600)
    @GetMapping(value = "/getAllProductType")
    public List<String> getProductType() {
        return fileManagementService.getAllProductType();
    }

    @ApiOperation(value = "根据文件类型获取所有版本信息",notes = "根据文件类型获取所有版本信息")
    @CrossOrigin(origins ="*",maxAge =3600)
    @GetMapping(value = "/getVersionByType")
    public List<String> getVersionByType() {
        return fileManagementService.getVersionByType();
    }

}
