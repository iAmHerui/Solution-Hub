package com.h3c.solutionhub.common;

import com.h3c.solutionhub.config.HttpsClientRequestFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class RestTemplateTool {

//    url "https://170.0.0.36/redfish/v1/SessionService/Sessions"
//    HashMap<String, Object> map = new HashMap<>();
//        map.put("UserName","admin");
//        map.put("Password","Password@_");
//    System.err.println(response.getStatusCodeValue()); 响应码,如:401、302、404、500、200等
//    String token = response.getHeaders().get("X-Auth-Token").get(0);
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
}
