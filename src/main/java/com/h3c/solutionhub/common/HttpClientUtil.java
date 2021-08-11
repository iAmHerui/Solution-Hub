package com.h3c.solutionhub.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/*
 * 利用HttpClient进行请求的工具类:支持http
 */
@Slf4j
public class HttpClientUtil {

    public DefaultHttpClient client;

    @Test
    public void test() {
        try {
//            // H3C CAS CVM 虚拟化管理平台访问URL
//            String strCVMURL = "http://210.0.12.25:8080/cas";
//
//            // 获取Token的RESTful URL
//            String strAuthURL = "/casrs/operator/getAuthUrl";
//
//            // 获取单点登录Token
//            String strToken = GetSSOToken(strCVMURL + strAuthURL);
//
//            log.info(strToken);

//            /* 获取所有主机列表 */
//            getHost("http://210.0.12.25:8080/cas/casrs/host/");

//            String name = "hostPool_test";
//            addHostPool(name);
//            Long hostPoolId = getHostPoolIdByName(name);
//            System.out.println(hostPoolId);
//            addCluster(hostPoolId,"cluster_test");
//            Long clusterId = getClusterIdByName("cluster_test");
//            addHost("root","Sys@1234",hostPoolId,clusterId,"210.0.12.25");
//            System.out.println(clusterId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DefaultHttpClient newInstance(String managementIp) {
        if(client == null) {
            client = new DefaultHttpClient();
            client.getCredentialsProvider().setCredentials(
                    new AuthScope(managementIp,8080,"VMC RESTful Web Services"),
                    new UsernamePasswordCredentials("admin","Cloud@1234")
            );
        }
        return client;
    }

    // 获取单点登录Token
    public Boolean isConnect(String managementIp) throws Exception {
        String url = "http://"+managementIp+":8080/cas/casrs/host/";
        DefaultHttpClient client = newInstance(managementIp);
        HttpGet get = new HttpGet(url);
        get.addHeader("accept","application/xml");
        HttpResponse response = client.execute(get);
        int code = response.getStatusLine().getStatusCode();
        if(code == 200) {
            return true;
        } else {
            return false;
        }
    }

//    // 获取单点登录Token
//    private String GetSSOToken(String url) {
//        String strToken = "";
//        try {
//            DefaultHttpClient client = newInstance();
//            HttpGet httpGet = new HttpGet(url);
//            HttpResponse response = client.execute(httpGet);
//            HttpEntity entity = response.getEntity();
//            if(null != entity) {
//                InputStream in = entity.getContent();
//
//                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                DocumentBuilder builder = factory.newDocumentBuilder();
//                Document document = builder.parse(in);
//                document.getDocumentElement().normalize();
//
//                Element rootElement = document.getDocumentElement();
//                NodeList uriNode = rootElement.getElementsByTagName("uri");
//                Element element = (Element) uriNode.item(0);
//                strToken = element.getTextContent();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return strToken;
//    }

    // 创建主机池
    public Boolean addHostPool(String managementIp,String hostPoolName) throws Exception {
        String url = "http://"+managementIp+":8080/cas/casrs/hostpool/add/"+hostPoolName;
        DefaultHttpClient client = newInstance(managementIp);
        HttpPost post = new HttpPost(url);
        HttpResponse response = client.execute(post);
        System.out.println(response.getStatusLine());
//        System.out.println(EntityUtils.toString(response.getEntity()));
        return true;
    }

    // 根据主机池名字获取主机池ID
    public Long getHostPoolIdByName(String managementIp,String name) throws Exception {
        String url = "http://210.0.12.25:8080/cas/casrs/hostpool/all";
        DefaultHttpClient client = newInstance(managementIp);
        HttpGet get = new HttpGet(url);
        get.addHeader("accept","application/xml");
        HttpResponse response = client.execute(get);
        System.out.println(response.getStatusLine());

        HttpEntity entity = response.getEntity();
        if(null != entity) {
            InputStream in = entity.getContent();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(in);
            document.getDocumentElement().normalize();

            Element rootElement = document.getDocumentElement();
            NodeList uriNode = rootElement.getElementsByTagName("hostPool");

            for (int i = 0; i <uriNode.getLength() ; i++) {
                Node node = uriNode.item(i);
                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j <childNodes.getLength() ; j++) {
                    if (childNodes.item(j).getNodeType()==Node.ELEMENT_NODE) {
//                        System.out.print("----------"+childNodes.item(j).getNodeName() + ":");
//                        System.out.println("----------"+childNodes.item(j).getFirstChild().getNodeValue());

                        if(childNodes.item(j).getFirstChild().getNodeValue().equals(name)) {
                            return Long.valueOf(childNodes.item(j-1).getFirstChild().getNodeValue());
                        }

                    }
                }
            }
        }
        return 0L;
    }

    // 创建集群
    public Boolean addCluster(String managementIp,Long hostPoolId,String clusterName) throws Exception {
        String url = "http://210.0.12.25:8080/cas/casrs/cluster/add";
        DefaultHttpClient client = newInstance(managementIp);
        HttpPost post = new HttpPost(url);
        post.addHeader("Content-Type","application/xml");
        String xml =
                "<cluster>\n" +
                "    <hostPoolId>"+ hostPoolId +"</hostPoolId>\n" +
                "    <name>"+ clusterName +"</name>\n" +
                "    <description>Solution Hub Agent Create</description>\n" +
                "    <enableHA>1</enableHA>\n" +
                "    <priority>2</priority>\n" +
                "    <enableLB>1</enableLB>\n" +
                "    <persistTime>5</persistTime>\n" +
                "    <checkInterval>10</checkInterval>\n" +
                "    <enableIPM>0</enableIPM>\n" +
                "    <persistTimeIPM>0</persistTimeIPM>\n" +
                "    <checkIntervalIPM>0</checkIntervalIPM>\n" +
                "    <lbMonitorId>4</lbMonitorId>\n" +
                "    <HaMinHost>1</HaMinHost>\n" +
                "</cluster>";

        StringEntity entity = new StringEntity(xml,"utf-8");
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        System.out.println(response.getStatusLine());

        return true;
    }

    public Long getClusterIdByName(String managementIp,String name) throws Exception {
        String url = "http://"+managementIp+":8080/cas/casrs/cluster/name/"+name;
        DefaultHttpClient client = newInstance(managementIp);
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        System.out.println(response.getStatusLine());

        HttpEntity entity = response.getEntity();
        if(null != entity) {
            InputStream in = entity.getContent();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(in);
            document.getDocumentElement().normalize();

            Element rootElement = document.getDocumentElement();
            NodeList uriNode = rootElement.getElementsByTagName("id");
            Element element = (Element) uriNode.item(0);
            return Long.valueOf(element.getTextContent());
        }
        return 0L;
    }

    // 增加主机
    public Boolean addHost(String userName,
                            String password,
                            Long hostPoolId,
                            Long clusterId,
                            String nodeManagementIp) throws Exception{
        String url = "http://"+nodeManagementIp+":8080/cas/casrs/host/add";
        DefaultHttpClient client = newInstance(nodeManagementIp);
        HttpPost post = new HttpPost(url);
//        post.setHeader("Content-Type","text/xml;charset=UTF-8");
        post.addHeader("Content-Type","application/xml");
        String xml = "<host>\n" +
                "<user>"+userName+"</user>\n" +
                "<pwd>"+password+"</pwd>\n" +
                "<hostPoolId>"+hostPoolId+"</hostPoolId>\n" +
                "<clusterId>"+clusterId+"</clusterId>\n" +
                "<name>"+nodeManagementIp+"</name>\n" +
                "<enableHA>1</enableHA>\n" +
                "<ignore>false</ignore>\n" +
                "</host>";

        StringEntity entity = new StringEntity(xml,"utf-8");
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        System.out.println(response.getStatusLine());

        return true;
    }

}
