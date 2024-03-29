package com.treefintech.b2b.oncecenter.common.utils;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * 支付宝接口---根据银行卡号获取银行编码
 * @author whb
 * @date 2018年9月28日 下午3:31:12
 * @Description: 根据银行卡号获取对应的银行变化如：ICBC
 * https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardBinCheck=true&cardNo=6217360599000454941
 * {"cardType":"DC","bank":"ZJNX","key":"6217360599000454941","messages":[],"validated":true,"stat":"ok"}
 */
public class BankCardNoUtil {

    private static Logger log = LoggerFactory.getLogger(BankCardNoUtil.class);

    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    private static int TIMEOUT = 1800000;

    private static RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(TIMEOUT)
            .setSocketTimeout(TIMEOUT).build();


    private static String RequestUrl = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardBinCheck=true&cardNo=";

    /**
     * 根据银行卡号获取对应的银行变化如：ICBC
     *
     * @param bankCardId
     * @return
     */
    public static String getBankCardNo(String bankCardId) {
        try {
            String result = getString(RequestUrl + bankCardId, "UTF-8");
            System.out.println("getBankCardNo for result = " + result);
        } catch (Exception e) {
            log.info("获取银行卡有误");
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(BankCardNoUtil.getBankCardNo("6217360599000454941"));
    }


    /**
     * get方式调用http,返回byte[]
     *
     * @param String url
     * @return byte[]
     * @throws Exception
     */
    private static byte[] getBytes(String url) throws Exception {
        HttpGet request = null;
        try {
            // HTTP请求
            request = new HttpGet(url);
            request.setConfig(requestConfig);
            log.debug(request.getRequestLine().toString());

            // 发送请求，返回响应
            HttpResponse response = httpClient.execute(request);
            // 响应成功
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                InputStream stream = response.getEntity().getContent();
                return IOUtils.toByteArray(stream);
            }
        } catch (Exception e) {
            throw new Exception("GET请求失败:[" + url + "]", e);
        } finally {
            if (request != null) {
                try {
                    request.releaseConnection();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    /**
     * get方式调用http,返回String
     *
     * @param String url
     * @param String charsetName
     * @return String
     * @throws Exception
     */
    private static String getString(String url, String charsetName) throws Exception {
        byte[] bytes = getBytes(url);
        if (bytes == null || bytes.length <= 0) {
            return "";
        }
        return new String(bytes, charsetName);
    }

}
