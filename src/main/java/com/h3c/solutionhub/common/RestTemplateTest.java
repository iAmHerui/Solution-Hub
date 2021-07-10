package com.h3c.solutionhub.common;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.h3c.solutionhub.config.HttpsClientRequestFactory;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestTemplateTest {

    /**
     * RestTemplate 发送 HTTP GET请求 --- 测试
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void doHttpGetTest() throws UnsupportedEncodingException {
        // -------------------------------> 获取Rest客户端实例
        RestTemplate restTemplate = new RestTemplate();

        // -------------------------------> 解决(响应数据可能)中文乱码 的问题
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        converterList.remove(1); // 移除原来的转换器
        // 设置字符编码为utf-8
        HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converterList.add(1, converter); // 添加新的转换器(注:convert顺序错误会导致失败)
        restTemplate.setMessageConverters(converterList);

        // -------------------------------> (选择性设置)请求头信息
        // HttpHeaders实现了MultiValueMap接口
        HttpHeaders httpHeaders = new HttpHeaders();
        // 给请求header中添加一些数据
        httpHeaders.add("Herui", "这是一个大帅哥!");

        // -------------------------------> 注:GET请求 创建HttpEntity时,请求体传入null即可
        // 请求体的类型任选即可;只要保证 请求体 的类型与HttpEntity类的泛型保持一致即可
        String httpBody = null;
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpBody, httpHeaders);

        // -------------------------------> URI
        StringBuffer paramsURL = new StringBuffer("http://127.0.0.1:8080/restTemplate/doHttpGet");
        // 字符数据最好encoding一下;这样一来，某些特殊字符才能传过去(如:flag的参数值就是“&”,不encoding的话,传不过去)
        paramsURL.append("?flag=" + URLEncoder.encode("&", "utf-8"));
        URI uri = URI.create(paramsURL.toString());

        //  -------------------------------> 执行请求并返回结果
        // 此处的泛型  对应 响应体数据   类型;即:这里指定响应体的数据装配为String
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);

        // -------------------------------> 响应信息
        //响应码,如:401、302、404、500、200等
        System.err.println(response.getStatusCodeValue());
        Gson gson = new Gson();
        // 响应头
        System.err.println(gson.toJson(response.getHeaders()));
        // 响应体
        if(response.hasBody()) {
            System.err.println(response.getBody());
        }

    }

    @Test
    public void doHttpPostTest() throws UnsupportedEncodingException {
        // -------------------------------> 获取Rest客户端实例
        RestTemplate restTemplate = new RestTemplate();

        // -------------------------------> 解决(响应数据可能)中文乱码 的问题
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        converterList.remove(1); // 移除原来的转换器
        // 设置字符编码为utf-8
        HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converterList.add(1, converter); // 添加新的转换器(注:convert顺序错误会导致失败)
        restTemplate.setMessageConverters(converterList);

        // -------------------------------> (选择性设置)请求头信息
        // HttpHeaders实现了MultiValueMap接口
        HttpHeaders httpHeaders = new HttpHeaders();
        // 设置contentType
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        // 给请求header中添加一些数据
        httpHeaders.add("Herui", "这是一个大帅哥!");

        // ------------------------------->将请求头、请求体数据，放入HttpEntity中
        // 请求体的类型任选即可;只要保证 请求体 的类型与HttpEntity类的泛型保持一致即可
        // 这里手写了一个json串作为请求体 数据 (实际开发时,可使用fastjson、gson等工具将数据转化为json串)
        String httpBody = "{\"motto\":\"唉呀妈呀！脑瓜疼！\"}";
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpBody, httpHeaders);

        // -------------------------------> URI
        StringBuffer paramsURL = new StringBuffer("http://170.0.0.227:8080/restTemplate/doHttpPost");
        // 字符数据最好encoding一下;这样一来，某些特殊字符才能传过去(如:flag的参数值就是“&”,不encoding的话,传不过去)
        paramsURL.append("?flag=" + URLEncoder.encode("&", "utf-8"));
        URI uri = URI.create(paramsURL.toString());

        //  -------------------------------> 执行请求并返回结果
        // 此处的泛型  对应 响应体数据   类型;即:这里指定响应体的数据装配为String
        ResponseEntity<String> response =
                restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);

        // -------------------------------> 响应信息
        //响应码,如:401、302、404、500、200等
        System.err.println(response.getStatusCodeValue());
        Gson gson = new Gson();
        // 响应头
        System.err.println(gson.toJson(response.getHeaders()));
        // 响应体
        if(response.hasBody()) {
            System.err.println(response.getBody());
        }

    }

    @Test
    public void doHttpsGetTest() {
        // -------------------------------> 获取Rest客户端实例
        RestTemplate restTemplate = new RestTemplate(new HttpsClientRequestFactory());

        // -------------------------------> 解决(响应数据可能)中文乱码 的问题
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        converterList.remove(1); // 移除原来的转换器
        // 设置字符编码为utf-8
        HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converterList.add(1, converter); // 添加新的转换器(注:convert顺序错误会导致失败)
        restTemplate.setMessageConverters(converterList);

        // -------------------------------> (选择性设置)请求头信息
        // HttpHeaders实现了MultiValueMap接口
        HttpHeaders httpHeaders = new HttpHeaders();
        // 设置contentType
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        // 给请求header中添加一些数据
        httpHeaders.add("Herui", "这是一个大帅哥!");

        // ------------------------------->注：GET请求 创建HttpEntity时，请求体传入null即可
        // 请求体的类型任选即可；只要保证 请求体的类型与httpEntity类的泛型保持一致即可
        String httpBody = null;
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpBody, httpHeaders);

        // -------------------------------> URI
        StringBuffer paramsURL = new StringBuffer("https://tcc.taobao.com/cc/json/mobile_tel_segment.htm");
        // 字符数据最好encoding一下;这样一来，某些特殊字符才能传过去(如:flag的参数值就是“&”,不encoding的话,传不过去)
        paramsURL.append("?tel=" + "13140109793");
        URI uri = URI.create(paramsURL.toString());

        //  -------------------------------> 执行请求并返回结果
        // 此处的泛型  对应 响应体数据   类型;即:这里指定响应体的数据装配为String
        ResponseEntity<String> response =
                restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);

        // -------------------------------> 响应信息
        //响应码,如:401、302、404、500、200等
        System.err.println(response.getStatusCodeValue());
        Gson gson = new Gson();
        // 响应头
        System.err.println(gson.toJson(response.getHeaders()));
        // 响应体
        if(response.hasBody()) {
            System.err.println(response.getBody());
        }

    }

    /**
     * 注:关于HTTPS这里只给出了一个GET示例，使用HTTPS进行POST请求与HTTP进行POST请求几乎是一样的，
     *      只是创建的RestTemplate实例和协议不一样而已，其余的都一样;类比GET即可,这里就不再给出示例了。
     */
    @Test
    public void doHttpsPostTest() {
        // -------------------------------> 获取Rest客户端实例
        RestTemplate restTemplate = new RestTemplate(new HttpsClientRequestFactory());

        // -------------------------------> 解决(响应数据可能)中文乱码 的问题
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        converterList.remove(1); // 移除原来的转换器
        // 设置字符编码为utf-8
        HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converterList.add(1, converter); // 添加新的转换器(注:convert顺序错误会导致失败)
        restTemplate.setMessageConverters(converterList);

        // -------------------------------> (选择性设置)请求头信息
        // HttpHeaders实现了MultiValueMap接口
        HttpHeaders httpHeaders = new HttpHeaders();
        // 设置contentType
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        // 给请求header中添加一些数据
        httpHeaders.add("Herui", "这是一个大帅哥!");

        // ------------------------------->注：GET请求 创建HttpEntity时，请求体传入null即可
        // 请求体的类型任选即可；只要保证 请求体的类型与httpEntity类的泛型保持一致即可
        String httpBody = null;
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpBody, httpHeaders);

        // -------------------------------> URI
        StringBuffer paramsURL = new StringBuffer("https://170.0.0.36/redfish/v1/SessionService/Sessions");
        // 字符数据最好encoding一下;这样一来，某些特殊字符才能传过去(如:flag的参数值就是“&”,不encoding的话,传不过去)
        paramsURL.append("?tel=" + "13140109793");
        URI uri = URI.create(paramsURL.toString());

        //  -------------------------------> 执行请求并返回结果
        // 此处的泛型  对应 响应体数据   类型;即:这里指定响应体的数据装配为String
        ResponseEntity<String> response =
                restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);

        // -------------------------------> 响应信息
        //响应码,如:401、302、404、500、200等
        System.err.println(response.getStatusCodeValue());
        Gson gson = new Gson();
        // 响应头
        System.err.println(gson.toJson(response.getHeaders()));
        // 响应体
        if(response.hasBody()) {
            System.err.println(response.getBody());
        }

    }

    /**
     * 创建会话
     */
    @Test
    public void doHttpsPost_login() {
        // -------------------------------> 获取Rest客户端实例
        RestTemplate restTemplate = new RestTemplate(new HttpsClientRequestFactory());

        // -------------------------------> 解决(响应数据可能)中文乱码 的问题
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        converterList.remove(1); // 移除原来的转换器


        // -------------------------------> (选择性设置)请求头信息
        // HttpHeaders实现了MultiValueMap接口
        HttpHeaders httpHeaders = new HttpHeaders();
        // 设置contentType
        httpHeaders.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
        // 给请求header中添加一些数据
//        httpHeaders.add("Herui", "这是一个大帅哥!");

        // ------------------------------->注：GET请求 创建HttpEntity时，请求体传入null即可
        // 请求体的类型任选即可；只要保证 请求体的类型与httpEntity类的泛型保持一致即可
        String httpBody = null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("UserName","admin");
        map.put("Password","Password@_");
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<Map<String, Object>>(map, httpHeaders);


        // -------------------------------> URI
        StringBuffer paramsURL = new StringBuffer("https://170.0.0.36/redfish/v1/SessionService/Sessions");
        // 字符数据最好encoding一下;这样一来，某些特殊字符才能传过去(如:flag的参数值就是“&”,不encoding的话,传不过去)
//        paramsURL.append("?tel=" + "13140109793");
        URI uri = URI.create(paramsURL.toString());

        //  -------------------------------> 执行请求并返回结果
        // 此处的泛型  对应 响应体数据   类型;即:这里指定响应体的数据装配为String
        ResponseEntity response =
                restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Object.class);

        // -------------------------------> 响应信息
        //响应码,如:401、302、404、500、200等
        System.err.println(response.getStatusCodeValue());
        String token = response.getHeaders().get("X-Auth-Token").get(0);


    }

    @Test
    public void stringToJson() {
        String test = "{\"@odata.context\":\"/redfish/v1/$metadata#NetworkPort.NetworkPort\",\"@odata.etag\":\"W/\\\"1625801163\\\"\",\"@odata.id\":\"/redfish/v1/Chassis/1/NetworkAdapters/PCIeSlot1/NetworkPorts/1\",\"@odata.type\":\"#NetworkPort.v1_1_0.NetworkPort\",\"AssociatedNetworkAddresses\":[\"54:2B:DE:0B:F1:BC\"],\"Id\":\"1\",\"LinkStatus\":\"N/A\",\"Name\":\"1\",\"Oem\":{\"Public\":{\"BDF\":\"0000:5E:00.0\",\"PortType\":\"OpticalPort\"}},\"PhysicalPortNumber\":\"1\"}";

        JSONObject resultJson = JSON.parseObject(test);
        System.out.println(resultJson.get("AssociatedNetworkAddresses"));
        System.out.println(resultJson.getJSONArray("AssociatedNetworkAddresses").get(0));
        System.out.println(resultJson.get("Id"));
    }
}
