package com.h3c.solutionhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping(value = "/restTemplate")
public class RestTemplateController {

    // 实例化RestTemplate
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/doHttpGet")
    public String doHttpGet(HttpServletRequest request) throws UnsupportedEncodingException {
        System.out.println(request.getCharacterEncoding());
        // GET方式传输中文，需要转码
        String Herui = new String(request.getHeader("Herui").getBytes("ISO-8859-1"),"utf-8");
        System.out.println(Herui);
        System.out.println("flag的值为："+request.getParameter("flag"));
        return "doHttpGet OK!";
    }

    @PostMapping("/doHttpPost")
    public String doHttpPost(HttpServletRequest request,@RequestBody String jsonString) throws UnsupportedEncodingException {
        System.out.println(request.getCharacterEncoding());
        // 并不是请求体中的（中文）数据，需要转码
        String Herui = new String(request.getHeader("Herui").getBytes("ISO-8859-1"),"utf-8");
        System.out.println(Herui);
        System.out.println("flag的值为："+request.getParameter("flag"));
        // 获取请求体中的数据
        System.out.println("请求体中的数据为："+Herui);
        System.out.println(jsonString);
        return "doHttpPost OK!";
    }

}
