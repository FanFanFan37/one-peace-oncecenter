package com.treefintech.b2b.oncecenter.common.utils;

import com.alibaba.fastjson.JSON;
import com.treefinance.b2b.common.exceptions.ServiceException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(10000)
            .setConnectTimeout(10000)
            .setConnectionRequestTimeout(10000)
            .build();

    private static HttpClient instance = null;

    private HttpClient() {
    }

    public static HttpClient getInstance() {
        if (instance == null) {
            instance = new HttpClient();
        }
        return instance;
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     */

    public String sendHttpPost(String httpUrl) throws Exception {
        return sendHttpPost(httpUrl, String.class);
    }

    public <T> T sendHttpPost(String httpUrl, Class<T> clazz) throws Exception {
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        return sendHttpReuest(httpPost, clazz);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param params  参数(格式:key1=value1&key2=value2)
     */
    public String sendHttpPost(String httpUrl, String params) throws Exception {
        return sendHttpPost(httpUrl, params, String.class);
    }

    public String sendHttpPost(String httpUrl, String params, Map<String, String> headMap) throws Exception {
        return sendHttpPost(httpUrl, params, headMap, String.class);
    }

    public <T> T sendHttpPost(String httpUrl, String params, Class<T> clazz) throws Exception {
        return sendHttpPost(httpUrl, params, null, clazz);
    }

    public <T> T sendHttpPost(String httpUrl, String params, Map<String, String> headMap, Class<T> clazz) throws Exception {
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        //设置参数
        StringEntity stringEntity = new StringEntity(params, "UTF-8");
        stringEntity.setContentType("application/x-www-form-urlencoded");
        httpPost.setEntity(stringEntity);

        if (MapUtils.isNotEmpty(headMap)) {
            for (String key : headMap.keySet()) {
                httpPost.setHeader(key, headMap.get(key));
            }
        }
        return sendHttpReuest(httpPost, clazz);
    }


    /**
     * 发送 post请求
     *
     * @param httpUrl          地址
     * @param nameValuePairMap 参数
     */
    public String sendHttpPost(String httpUrl, Map<String, String> nameValuePairMap) throws Exception {
        return sendHttpPost(httpUrl, nameValuePairMap, String.class);
    }

    public String sendHttpPost(String httpUrl, Map<String, String> nameValuePairMap, Map<String, String> headMap) throws Exception {
        return sendHttpPost(httpUrl, nameValuePairMap, headMap, String.class);
    }

    public <T> T sendHttpPost(String httpUrl, Map<String, String> nameValuePairMap, Class<T> clazz) throws Exception {
        return sendHttpPost(httpUrl, nameValuePairMap, new HashMap<>(), clazz);
    }


    public <T> T sendHttpPost(String httpUrl, Map<String, String> nameValuePairMap, Map<String, String> headMap, Class<T> clazz) throws Exception {
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        // 创建参数队列
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (String key : nameValuePairMap.keySet()) {
            nameValuePairs.add(new BasicNameValuePair(key, String.valueOf(nameValuePairMap.get(key))));
        }

        if (MapUtils.isNotEmpty(headMap)) {
            for (String key : headMap.keySet()) {
                httpPost.setHeader(key, headMap.get(key));
            }
        }

        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        return sendHttpReuest(httpPost, clazz);
    }


    public <T> T sendHttpPost(String httpUrl, byte[] bytes, Class<T> clazz) throws Exception {
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        // 创建参数队列
        HttpEntity entity = new ByteArrayEntity(bytes);
        httpPost.setEntity(entity);
        return sendHttpReuest(httpPost, clazz);
    }

    public <T> T sendHttpPost(String httpUrl, byte[] bytes, Map<String, String> headMap, Class<T> clazz) throws Exception {
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        // 创建参数队列
        HttpEntity entity = new ByteArrayEntity(bytes);
        httpPost.setEntity(entity);

        if (MapUtils.isNotEmpty(headMap)) {
            for (String key : headMap.keySet()) {
                httpPost.setHeader(key, headMap.get(key));
            }
        }
        return sendHttpReuest(httpPost, clazz);
    }

    /**
     * 发送 post请求（带文件）
     *
     * @param httpUrl   地址
     * @param maps      参数
     * @param fileLists 附件
     */
    public String sendHttpPost(String httpUrl, Map<String, String> maps, List<File> fileLists) throws Exception {
        return sendHttpPost(httpUrl, maps, fileLists, String.class);
    }


    public <T> T sendHttpPost(String httpUrl, Map<String, String> maps, List<File> fileLists, Class<T> clazz) throws Exception {
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
        for (String key : maps.keySet()) {
            meBuilder.addPart(key, new StringBody(maps.get(key), ContentType.TEXT_PLAIN));
        }
        for (File file : fileLists) {
            FileBody fileBody = new FileBody(file);
            meBuilder.addPart("files", fileBody);
        }
        HttpEntity reqEntity = meBuilder.build();
        httpPost.setEntity(reqEntity);
        return sendHttpReuest(httpPost, clazz);
    }

    /**
     * 发送Post请求
     *
     * @param httpRequest
     * @return
     */
    private <T> T sendHttpReuest(HttpRequestBase httpRequest, Class<T> clazz) throws Exception {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            // 创建默认的httpClient实例.
            httpClient = HttpClients.createDefault();
            httpRequest.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(httpRequest);
            return readEntity(response, clazz);
        } finally {
            closeHttp(httpClient, response);
        }
    }

    private void closeHttp(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        try {
            // 关闭连接,释放资源
            if (response != null) {
                response.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> T readResponse(CloseableHttpResponse response, Class<T> clazz) throws Exception {
        int status = getStatus(response);
        if (status >= 200 && status < 300) {
            return readEntity(response, clazz);
        } else {
            String body = readEntity(response);
            throw new ServiceException(status, body);
        }
    }

    /**
     * 发送 get请求
     *
     * @param httpUrl
     */
    public String sendHttpGet(String httpUrl, Map<String, String> headsMap) throws Exception {
        return sendHttpGet(httpUrl, headsMap, String.class);
    }

    public <T> T sendHttpGet(String httpUrl, Class<T> clazz) throws Exception {
        return sendHttpGet(httpUrl, null, clazz);
    }

    /**
     * @param httpUrl
     * @param headMap 需要放到头部的字段信息
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T sendHttpGet(String httpUrl, Map<String, String> headMap, Class<T> clazz) throws Exception {
        HttpGet httpGet = new HttpGet(httpUrl);
        if (MapUtils.isNotEmpty(headMap)) {
            for (String key : headMap.keySet()) {
                httpGet.setHeader(key, headMap.get(key));
            }
        }
        return sendHttpReuest(httpGet, clazz);
    }

    /**
     * 发送 get请求
     *
     * @param httpUrl
     */
    public String sendHttpGet(String httpUrl) throws Exception {
        return sendHttpGet(httpUrl, null, String.class);
    }

    /**
     * 发送 get请求Https
     *
     * @param httpUrl
     */
    public String sendHttpsGet(String httpUrl) throws Exception {
        return sendHttpsGet(httpUrl, String.class);
    }

    public <T> T sendHttpsGet(String httpUrl, Class<T> clazz) throws Exception {
        HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
        return sendHttpsGet(httpGet, clazz);
    }


    /**
     * 发送Get请求Https
     *
     * @param httpGet
     * @return
     */
    private <T> T sendHttpsGet(HttpGet httpGet, Class<T> clazz) throws Exception {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            // 创建默认的httpClient实例.
            PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.load(new URL(httpGet.getURI().toString()));
            DefaultHostnameVerifier hostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcher);
            httpClient = HttpClients.custom().setSSLHostnameVerifier(hostnameVerifier).build();
            httpGet.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(httpGet);
            return readResponse(response, clazz);
        } finally {
            closeHttp(httpClient, response);
        }
    }


    private static int getStatus(HttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    @SuppressWarnings("unchecked")
    private static <T> T readEntity(HttpResponse response, Class<T> clazz) throws IOException {
        if (clazz == null) {
            return (T) response.getEntity();
        } else if (clazz == byte[].class) {
            return clazz.cast(readToByteArray(response.getEntity()));
        } else if (clazz == String.class) {
            return clazz.cast(readEntity(response));
        } else {
            HttpEntity entity = response.getEntity();
            return entity != null ? JSON.parseObject(entity.getContent(), clazz) : null;
        }
    }

    private static String readEntity(HttpResponse response) throws IOException {
        String body = readToString(response.getEntity());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[HttpClient] >> response body : {}", body);
        }
        return body;
    }

    private static String readToString(HttpEntity entity) throws IOException {
        return entity == null ? "" : EntityUtils.toString(entity, Consts.UTF_8);
    }

    private static byte[] readToByteArray(HttpEntity entity) throws IOException {
        return entity == null ? ArrayUtils.EMPTY_BYTE_ARRAY : EntityUtils.toByteArray(entity);
    }

}