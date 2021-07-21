package com.h3c.solutionhub.common;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/*
 * 利用HttpClient进行请求的工具类
 */
public class HttpClientUtil {

    public HttpResponse sendHttpsGet(String url, Map map,String token) {
        HttpClient httpClient = null;
        HttpGet httpGet = null;
        HttpResponse response = null;
        try {
            httpClient = new SSLClient();
            httpGet = new HttpGet(url);
            httpGet.setHeader("Content-Type","application/json");
            if(token!=null) {
                httpGet.setHeader("X-Auth-Token",token);
            }
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
//            System.out.println("响应内容为:" + EntityUtils.toString(httpEntity));
            String string = EntityUtils.toString(httpEntity);
            System.out.println("MAC:"+string.substring(string.indexOf("[\"")+2,string.lastIndexOf("\"]")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return response;
    }

    public HttpResponse sendHttpsPost(String url, Map map,String token) {
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        HttpResponse response = null;
        try {
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type","application/json");
            if(token!=null) {
                httpPost.setHeader("X-Auth-Token",token);
            }

            ObjectMapper mapper = new ObjectMapper();
            String writeValueAsString = mapper.writeValueAsString(map);
            JSONObject jsonParam = JSONObject.parseObject(writeValueAsString);

            StringEntity entity = new StringEntity(jsonParam.toString(), HTTP.UTF_8);
            httpPost.setEntity(entity);

            response = httpClient.execute(httpPost);
//            Header[] headers = response.getAllHeaders();
//            System.out.println("响应状态为:" + response.getStatusLine());
//            for(Header header:headers) {
//                if(header.getName().equals("X-Auth-Token")) {
//                    System.out.println(header);
//                    System.out.println(header.getValue());
//                }
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public HttpResponse sendHttpsPatch(String url, Map map,String token) {
        HttpClient httpClient = null;
        HttpPatch httpPatch = null;
        HttpResponse response = null;
        try {
            httpClient = new SSLClient();
            httpPatch = new HttpPatch(url);

            httpPatch.setHeader("Content-Type","application/json");
            httpPatch.setHeader("X-Auth-Token",token);

            ObjectMapper mapper = new ObjectMapper();
            String writeValueAsString = mapper.writeValueAsString(map);
            JSONObject jsonParam = JSONObject.parseObject(writeValueAsString);

            StringEntity entity = new StringEntity(jsonParam.toString(), HTTP.UTF_8);
            httpPatch.setEntity(entity);

            response = httpClient.execute(httpPatch);
            System.out.println("响应状态为:" + response.getStatusLine());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public String doPatch2(String url, Map map,String token) {
        HttpClient httpClient = null;
        HttpPatch httpPatch = null;
        String result = null;
        try {
            httpClient = new SSLClient();
            httpPatch = new HttpPatch(url);

            httpPatch.setHeader("Content-Type","application/json");
            httpPatch.setHeader("X-Auth-Token",token);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> elem = (Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                httpPatch.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPatch);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "utf-8");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
