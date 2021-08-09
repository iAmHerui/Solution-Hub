package com.h3c.solutionhub.common;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/*
 * 利用HttpClient进行请求的工具类:支持http
 */
@Slf4j
public class HttpClientUtil {

    public static DefaultHttpClient client;

    public static void main(String[] args) throws Exception{
        // H3C CAS CVM 虚拟化管理平台访问URL
        String strCVMURL = "http://210.0.12.25:8080/cas";

        // 获取Token的RESTful URL
        String strAuthURL = "/casrs/operator/getAuthUrl";

        // 获取单点登录Token
        String strToken = GetSSOToken(strCVMURL + strAuthURL);

        log.info(strToken);

        /* 获取所有主机列表 */
        getHost("http://210.0.12.25:8080/cas/casrs/host/");
    }

    private static DefaultHttpClient newInstance() {
        if(client == null) {
            client = new DefaultHttpClient();
            client.getCredentialsProvider().setCredentials(
                    new AuthScope("210.0.12.25",8080,"VMC RESTful Web Services"),
                    new UsernamePasswordCredentials("admin","Sys@1234")
            );
        }
        return client;
    }

    // 获取单点登录Token
    private static String GetSSOToken(String url) {
        String strToken = "";
        try {
            DefaultHttpClient client = newInstance();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if(null != entity) {
                InputStream in = entity.getContent();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(in);
                document.getDocumentElement().normalize();

                Element rootElement = document.getDocumentElement();
                NodeList uriNode = rootElement.getElementsByTagName("uri");
                Element element = (Element) uriNode.item(0);
                strToken = element.getTextContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strToken;
    }

    // 获取所有主机列表
    public static void getHost(String url) throws Exception {
        DefaultHttpClient client = newInstance();
        HttpGet get = new HttpGet(url);
        get.addHeader("accept","application/xml");
        HttpResponse response = client.execute(get);
        System.out.println(response.getStatusLine());
        System.out.println(EntityUtils.toString(response.getEntity()));
    }
}
