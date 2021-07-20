package com.h3c.solutionhub.common;

import com.h3c.solutionhub.config.HttpsClientRequestFactory;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class RestTemplateTool {

    public ResponseEntity sendHttps(String url,Map map,HttpMethod httpMethod,String token) {
        // 获取Rest客户端实例
        RestTemplate restTemplate = new RestTemplate(new HttpsClientRequestFactory());

        // 解决(响应数据可能)中文乱码 的问题
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        converterList.remove(1); // 移除原来的转换器

        // 请求头信息
        HttpHeaders httpHeaders = new HttpHeaders();
        // 设置contentType
        httpHeaders.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
        if(token!=null) {
            httpHeaders.set("X-Auth-Token",token);
        }

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<Map<String, Object>>(map, httpHeaders);

        // URI
        StringBuffer paramsURL = new StringBuffer(url);
        URI uri = URI.create(paramsURL.toString());

        //  执行请求并返回结果
        ResponseEntity response =
                restTemplate.exchange(uri, httpMethod, httpEntity, Object.class);
        return response;
    }

    // RestTemplate发送HTTP PATCH请求
    public ResponseEntity sendHttpsPatch(String url,Map map,String token) {

        StringBuffer forwardURL = new StringBuffer();
        forwardURL.append("http://").append(url);

        //创建 restTemplate 对象
        RestTemplate restTemplatePatch = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        //方法一：使用exchange
        //封装请求头
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<String,String>();
//        headers.add("Accept","application/json");
        headers.add("Content-Type","application/json");
        if(token!=null) {
            headers.add("X-Auth-Token",token);
        }
        //封装请求内容
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity(map,headers);
        ResponseEntity responseEntity = restTemplatePatch.exchange(forwardURL.toString(), HttpMethod.PATCH, requestEntity, Object.class);

        //方法二：使用patchForObject
//        ResponseEntity responseEntity = restTemplatePatch.patchForObject(forwardURL.toString(), paramObject, ResponseEntity.class, paramMap);

        return responseEntity;
    }

    // TODO RestTemplate发送HTTPs PATCH请求

}
