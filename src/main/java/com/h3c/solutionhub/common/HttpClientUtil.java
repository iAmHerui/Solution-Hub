package com.h3c.solutionhub.common;

import com.h3c.solutionhub.entity.NodeBo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * 利用HttpClient进行请求的工具类:支持http
 */
@Slf4j
public class HttpClientUtil {

    public DefaultHttpClient client;

    @Value("${casUserName}")
    private String casUserName;

    @Value("${casPassword}")
    private String casPassword;

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

//            String name = "sd";
//
//            String ip = "210.0.12.27";
////            Boolean result = isConnect(ip);
//
////            addHostPool(ip, name);
//            Long hostPoolId = getHostPoolIdByName(ip, name);
//            System.out.println(hostPoolId);
////            addCluster(ip, hostPoolId, "cluster_test");
//
//            Long clusterId = getClusterIdByName(ip,"xx");
//            addHost("root","Sys@1234",hostPoolId,clusterId,"210.0.12.27","210.0.12.26");
////            System.out.println(clusterId);

            queryMsgInfo("210.0.12.27",1630044972237l);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DefaultHttpClient newInstance(String managementIp) {
//        if(client == null) {
        client = new DefaultHttpClient();
        client.getCredentialsProvider().setCredentials(
                new AuthScope(managementIp,8080,"VMC RESTful Web Services"),
                new UsernamePasswordCredentials("admin","Cloud@1234")
        );
//        }
        return client;
    }

    // 获取单点登录Token
    public Boolean isConnect(String managementIp) {
        String url = "http://"+managementIp+":8080/cas/casrs/host/";
        DefaultHttpClient client = newInstance(managementIp);
        HttpGet get = new HttpGet(url);
        get.addHeader("accept","application/xml");
        HttpResponse response = null;
        try {
            response = client.execute(get);
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
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
        String url = "http://"+managementIp+":8080/cas/casrs/hostpool/all";
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
        String url = "http://"+managementIp+":8080/cas/casrs/cluster/add";
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
//        System.out.println(response.getStatusLine());

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
                           String managementIp,
                           String nodeManagementIp) throws Exception{
        String url = "http://"+managementIp+":8080/cas/casrs/host/add";
        DefaultHttpClient client = newInstance(managementIp);
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
        log.info("添加主机 "+nodeManagementIp+" ,http状态码: "+response.getStatusLine());

        if(response.getStatusLine().getStatusCode()==200) {
            HttpEntity httpEntity = response.getEntity();

            log.info("主机添加任务已下发,等待1min 查看任务状态");
            TimeUnit.SECONDS.sleep(60);

            // 获取任务ID
            if(null != httpEntity) {
                InputStream in = httpEntity.getContent();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(in);
                document.getDocumentElement().normalize();

                Element rootElement = document.getDocumentElement();
                NodeList uriNode = rootElement.getElementsByTagName("msgId");
                Element element = (Element) uriNode.item(0);
                Long msgId = Long.valueOf(element.getTextContent());
                log.info("任务ID: "+msgId);

                // 增加“检测任务是否成功”,最多重试5次
                int count = 5;
                for(int i =1;i<=count;i++) {
                    Boolean result = queryMsgInfo(managementIp,msgId);
                    log.info("检测添加主机任务,第 "+i+" 次,检测结果: "+result);
                    if(result) {
                        return true;
                    } else {
                        // 任务未执行成功,等待1min
                        TimeUnit.SECONDS.sleep(60);
                    }
                }
            }
        }
        return false;
    }

    // 查询任务详细信息
    public Boolean queryMsgInfo(String managementIp,Long msgId) throws Exception {
        String url = "http://"+managementIp+":8080/cas/casrs/message/"+msgId;
        DefaultHttpClient client = newInstance(managementIp);
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity httpEntity = response.getEntity();

        // 获取任务ID
        if(null != httpEntity) {
            InputStream in = httpEntity.getContent();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(in);
            document.getDocumentElement().normalize();

            Element rootElement = document.getDocumentElement();
            NodeList uriNode = rootElement.getElementsByTagName("detail");
            Element element = (Element) uriNode.item(0);
            log.info(element.getTextContent());
            NodeList uriNode1 = rootElement.getElementsByTagName("completed");
            Element element1 = (Element) uriNode1.item(0);
            log.info(element1.getTextContent());
            return Boolean.valueOf(element1.getTextContent());
        }
        return false;
    }

}
