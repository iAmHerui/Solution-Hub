package com.h3c.solutionhub.controller;

import com.h3c.solutionhub.common.JsonResult;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

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

    @Value("${uploadFolder}")
    private String filePath;

    /**
     * 仅适用小文件
     * springboot项目自带的tomcat对上传的文件大小有默认的限制
     *
     * @param file
     * @param request
     * @return
     */
    @ApiOperation(value = "小文件上传",notes = "小文件上传，目前上传路径是固定的，需修改源码。")
    @PostMapping(value = "/uploadSmallFile",headers = "content-type=multipart/form-data")
    public Map uploadSmallFile(@RequestParam(value = "file", required = true) MultipartFile file, HttpServletRequest request) {
        return uploadFileService.uploadFile(file,request);
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
    @RequestMapping("/upload")
    public void bigFile(HttpServletRequest request,
                        HttpServletResponse response,
                        String guid,
                        @RequestParam(defaultValue = "0") Integer chunk,
                        MultipartFile file,
                        Integer chunks) {
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (isMultipart) {
                // 临时目录用来存放所有分片文件
                String tempFileDir = filePath + guid;
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
     * @param guid
     * @param fileBO
     * @throws Exception
     */
    @RequestMapping("/merge")
    @ResponseBody
    public JsonResult mergeFile(String guid,
                                FileBO fileBO) {
        // 得到 destTempFile 就是最终的文件
        try {
            File parentFileDir = new File(filePath + guid);
            if (parentFileDir.isDirectory()) {
                File destTempFile = new File(filePath + "/merge", fileBO.getFileName());
                // 文件是否已存在？
                if (!destTempFile.exists()) {
                    //先得到文件的上级目录，并创建上级目录，在创建文件,
                    destTempFile.getParentFile().mkdir();
                    try {
                        // 创建文件，但此时没有数据
                        destTempFile.createNewFile(); //上级目录没有创建，这里会报错
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 把分片数据合入文件
                    for (int i = 0; i < parentFileDir.listFiles().length; i++) {
                        File partFile = new File(parentFileDir, guid + "_" + i + ".part");
                        FileOutputStream destTempfos = new FileOutputStream(destTempFile, true);
                        //遍历"所有分片文件"到"最终文件"中
                        FileUtils.copyFile(partFile, destTempfos);
                        destTempfos.close();
                    }

                    // 录入数据库
                    fileManagementService.insertFileInfo(fileBO);

                    // 删除临时目录中的分片文件
                    FileUtils.deleteDirectory(parentFileDir);
                    return JsonResult.success();
                } else {
                    // 删除临时目录中的分片文件
                    FileUtils.deleteDirectory(parentFileDir);
                    log.error("文件已存在！");
                    return JsonResult.failMessage("文件已存在！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.fail();
        }
        return null;
    }



    @ApiOperation(value = "文件删除",notes = "文件删除")
    @PostMapping(value = "/fileDelete")
    public Boolean fileDelete(String fileName) {
        return fileManagementService.deleteFileInfo(fileName);
    }
}
