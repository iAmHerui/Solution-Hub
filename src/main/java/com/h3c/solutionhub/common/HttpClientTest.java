package com.h3c.solutionhub.common;

import com.alibaba.fastjson.JSONObject;
import com.h3c.solutionhub.entity.NodeBo;
import org.apache.http.HttpEntity;
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

        String url = "https://210.0.12.23/redfish/v1/Systems/1";

        HashMap<String, Object> map = new HashMap<>();
        map.put("AssetTag","solutionhub");
        map.put("HostName","solutionhub");
        HashMap<String, Object> childMap = new HashMap<>();
        childMap.put("BootSourceOverrideMode","UEFI");
        childMap.put("BootSourceOverrideTarget","Pxe");
        childMap.put("BootSourceOverrideEnabled","Once");

        map.put("Boot",childMap);

        httpClientUtil.sendHttpsPatch2(url,map,"Lnee7pxOb6YF/UdDzcraI46XgocPeb8+JzRs8XTadq0=");
    }

    @Test
    public void HttpsPost() {
        HttpClientUtil httpClientUtil = new HttpClientUtil();

        String url = "https://210.0.12.23/redfish/v1/SessionService/Sessions";

        HashMap<String, Object> map = new HashMap<>();
        map.put("UserName","admin");
        map.put("Password","Password@_");

        httpClientUtil.sendHttpsPost(url,map,"");
    }

    @Test
    public void HttpsGet() {
        HttpClientUtil httpClientUtil = new HttpClientUtil();

        String url = "https://210.0.12.23/redfish/v1/Chassis/1/NetworkAdapters/mLOM/NetworkPorts/1";

        HttpResponse response = new HttpClientUtil().sendHttpsGet(url,null,"0ny1TUTv3IdP/mImBuOzx+T6IQYSEzrUcibAl5GW4zg=");

        HttpEntity httpEntity = response.getEntity();
        NodeBo nodeBo = new NodeBo();
//        String string = "";
        try {
//            System.out.println(EntityUtils.toString(httpEntity));
            String string = EntityUtils.toString(httpEntity);
            String mac = string.substring(string.indexOf("[\"")+2,string.lastIndexOf("\"]"));
            System.out.println("MAC: "+mac);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void HttpsPost_reboot() {
        String url = "https://210.0.12.23/redfish/v1/Systems/1/Actions/ComputerSystem.Reset";

        HashMap<String, Object> map = new HashMap<>();
        map.put("ResetType","ForceRestart");

        HttpResponse response = new HttpClientUtil().sendHttpsPost(url,map,"Lnee7pxOb6YF/UdDzcraI46XgocPeb8+JzRs8XTadq0=");
        System.out.println("reboot 响应状态为:" + response.getStatusLine());
    }
}
