package com.h3c.solutionhub.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

public class HttpClientTest {

    @Test
    public void HttpPatch() {
        JSONObject resultObj = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPatch httpPatch = new HttpPatch("http://localhost:8080/restTemplate/testPatch");
        httpPatch.setHeader("Content-type", "application/json");
        httpPatch.setHeader("Charset", HTTP.UTF_8);
        httpPatch.setHeader("Accept", "application/json");
        httpPatch.setHeader("Accept-Charset", HTTP.UTF_8);
        try {
            StringEntity entity = new StringEntity("{\n\t\"instances\":0\n}", HTTP.UTF_8);
            httpPatch.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPatch);
//            resultObj = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void HttpsPatch() {
        HttpClientUtil httpClientUtil = new HttpClientUtil();

        String url = "https://170.0.0.36/redfish/v1/Systems/1";

        HashMap<String, Object> map = new HashMap<>();
        map.put("AssetTag","solution_hub");
        map.put("HostName","solution_hub");
        HashMap<String, Object> childMap = new HashMap<>();
        childMap.put("BootSourceOverrideMode","UEFI");
        childMap.put("BootSourceOverrideTarget","Pxe");
        childMap.put("BootSourceOverrideEnabled","Once");

        map.put("Boot",childMap);

        httpClientUtil.doPatch(url,map,"W3zFhEa8vtbvR/RBJDM2hb25ZD/2r3PEVARueoMV5Mk=");
    }
}
