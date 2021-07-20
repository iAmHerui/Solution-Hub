package com.h3c.solutionhub.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
/*
 * 利用HttpClient进行请求的工具类
 */
public class HttpClientUtil {
    public String doPost(String url, Map<String, String> map, String charset) {
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> elem = (Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public HttpResponse doPatch(String url, Map map,String token) {
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
