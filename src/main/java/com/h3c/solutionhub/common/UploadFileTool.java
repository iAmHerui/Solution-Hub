package com.h3c.solutionhub.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadFileTool {

    @Value("${uploadFolder}")
    private static String filePath;

    public static Map fileUpload(MultipartFile file, HttpServletRequest request) {
        Map result = new HashMap();

        if(file!=null) {

            // 获取文件原名称
            String fileName = file.getOriginalFilename();

            // 判断文件路径
            String type = fileName.indexOf(".")!=-1?fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()):null;

            // 项目在容器中实际发布运行的根路径
            String realPath = request.getSession().getServletContext().getRealPath("/");

            // 设置存放图片文件的路径
            String path = filePath+System.getProperty("file.separator")+fileName;

            // 转存文件到指定的路径,如果文件重名则会抛异常
            try {
                file.transferTo(new File(path));

                result.put("path",path);
                result.put("code",true);
                result.put("msg","文件上传成功");
            } catch (IOException e) {
                result.put("code",false);
                result.put("msg",e.getMessage());
            }

        }  else {
            result.put("code",false);
            result.put("msg","文件不能为空！");
        }

        return result;
    }


}
