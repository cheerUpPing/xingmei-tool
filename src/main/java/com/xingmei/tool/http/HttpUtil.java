package com.xingmei.tool.http;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1.发送http请求 <br>
 * 2.发送https请求 <br>
 */
public class HttpUtil {

    private static CloseableHttpClient httpClient;

    private static CloseableHttpClient httpsClientUnsafe;
    //TODO 暂时还不支持加载特定证书
    private static CloseableHttpClient httpsClientSafe;

    static {
        try {
            httpClient = HttpClients.createDefault();

            httpsClientUnsafe = HttpUtil.getHttpsClientUnsafe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送http get请求
     *
     * @param requestUrl
     * @param headerMap
     * @return
     */
    public static String sendHttpGet(String requestUrl, Map<String, String> headerMap, Map<String, String> requestParamMap) {
        return HttpUtil.get(HttpType.HTTP, requestUrl, headerMap, requestParamMap);
    }

    /**
     * 发送https get请求 相信所有证书
     *
     * @param requestUrl
     * @param headerMap
     * @return
     */
    public static String sendHttpsGet(String requestUrl, Map<String, String> headerMap, Map<String, String> requestParamMap) {
        return HttpUtil.get(HttpType.HTTPS_UNSAFE, requestUrl, headerMap, requestParamMap);
    }

    private static String get(HttpType httpType, String requestUrl, Map<String, String> headerMap, Map<String, String> requestParamMap) {
        if (MapUtils.isNotEmpty(requestParamMap)) {
            StringBuilder requestUrlBuilder = new StringBuilder(requestUrl + "?");
            for (Map.Entry<String, String> entry : requestParamMap.entrySet()) {
                if (StringUtils.isNotBlank(entry.getKey())) {
                    requestUrlBuilder.append(entry.getKey().trim()).append("=")
                            .append(StringUtils.isNotBlank(entry.getValue()) ? entry.getValue().trim() : "&");
                }
            }
            requestUrl = requestUrlBuilder.toString();
            if (requestUrl.endsWith("&")) {
                requestUrl = requestUrl.substring(0, requestUrl.length() - 1);
            }
        }
        // get method
        HttpGet httpGet = new HttpGet(requestUrl);

        // set header
        if (MapUtils.isNotEmpty(headerMap)) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }

        //response
        HttpResponse response = null;
        //get response into String
        String temp = "";
        try {
            response = HttpUtil.getHttpClient(httpType).execute(httpGet);
            if (!"200".equals("" + response.getStatusLine().getStatusCode())) {
                throw new RuntimeException(response.getStatusLine().getReasonPhrase());
            }
            HttpEntity entity = response.getEntity();
            temp = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
        return temp;
    }

    /**
     * 发生http post body请求
     *
     * @param requestUrl
     * @param headerMap
     * @param paramObj
     * @return
     */
    public static String sendHttpPost(String requestUrl, Map<String, String> headerMap, Object paramObj) {
        return sendPostBody(HttpType.HTTP, requestUrl, headerMap, paramObj);
    }

    /**
     * 发生https post body请求
     *
     * @param requestUrl
     * @param headerMap
     * @param paramObj
     * @return
     */
    public static String sendHttpsPost(String requestUrl, Map<String, String> headerMap, Object paramObj) {
        return sendPostBody(HttpType.HTTPS_UNSAFE, requestUrl, headerMap, paramObj);
    }

    /**
     * 发生http post body请求
     *
     * @param requestUrl
     * @param headerMap
     * @param paramObj
     * @return
     */
    private static String sendPostBody(HttpType httpType, String requestUrl, Map<String, String> headerMap, Object paramObj) {
        // get method
        HttpPost httpPost = new HttpPost(requestUrl);

        // set header
        if (MapUtils.isNotEmpty(headerMap)) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        //response
        HttpResponse response = null;
        //get response into String
        String temp = "";
        try {
            if (paramObj != null && !"".equals(paramObj)) {
                StringEntity entity = new StringEntity(JSON.toJSONString(paramObj), "application/json", "UTF-8");
                httpPost.setEntity(entity);
            }
            response = HttpUtil.getHttpClient(httpType).execute(httpPost);
            if (!"200".equals("" + response.getStatusLine().getStatusCode())) {
                throw new RuntimeException(response.getStatusLine().getReasonPhrase());
            }
            HttpEntity responseEntity = response.getEntity();
            temp = EntityUtils.toString(responseEntity, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e.getCause());
        }
        return temp;
    }

    /**
     * 获取https客户端，信任所有证书，不需要加载keystore
     *
     * @return
     * @throws Exception
     */
    private static CloseableHttpClient getHttpsClientUnsafe() throws Exception {

        SSLContext sslcontext = SSLContexts.custom().build();
        sslcontext.init(null, new X509TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }}, new SecureRandom());
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

        return HttpClients.custom().setSSLSocketFactory(factory).build();
    }

    private static CloseableHttpClient getHttpClient(HttpType httpType) {
        if (httpType == HttpType.HTTP) {
            return httpClient;
        } else if (httpType == HttpType.HTTPS_UNSAFE) {
            return httpsClientUnsafe;
        } else if (httpType == HttpType.HTTPS_SAFE) {
            return httpsClientSafe;
        } else {
            return httpClient;
        }
    }

    /**
     * http类型 <br>
     * HTTP         发送http协议 <br>
     * HTTPS_UNSAFE 发送https协议,信任任意证书 忽略keystore <br>
     * HTTPS_SAFE   发送https协议,需要加载keystore <br>
     */
    private enum HttpType {
        HTTP,
        HTTPS_UNSAFE,
        HTTPS_SAFE;
    }

    public static void main(String[] args) {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("name", "小红");
        headerMap.put("age", 12);
        String res = (String) HttpUtil.sendHttpsPost("http://localhost:8080/studydemo/request/param/queryUserObj", null, headerMap);
        System.out.println(res);
    }

}
