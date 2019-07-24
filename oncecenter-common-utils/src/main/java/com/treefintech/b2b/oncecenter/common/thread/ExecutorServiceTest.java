package com.treefintech.b2b.oncecenter.common.thread;

import com.alibaba.fastjson.JSONObject;
import com.treefinance.b2b.common.exceptions.ServiceException;
import com.treefinance.b2b.common.http.HttpClient;
import com.treefinance.b2b.common.utils.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangfan
 * @description
 * @date 2019/6/28 下午5:37
 **/
public class ExecutorServiceTest {
    public static List<String> codeList = new ArrayList<>();

    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolExecutor(20, 40, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), (ThreadFactory) Thread::new);
        for (int i = 0; i < 1; i++) {
            int finalI = i;
            executorService.execute(() -> {
                getLoanOrderInfo(codeList, finalI);
                System.out.println("count is = " + finalI);
            });
        }
        try {
            Thread.sleep(10000L);
            System.out.println(codeList);
            System.out.println(codeList.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void getLoanOrderInfo(List<String> codeList, Integer count) {
        Map<String, String> paramMap = new HashMap<>(6);
        paramMap.put("merchantCode", "BJZZJF190523001");
        paramMap.put("openid", "8BE33F308D362232C8471194255D85AE");
        paramMap.put("accessToken", "CG58ATyjsYTFzxNdw/n297MDuNMcAf9h1TeFcqnouyk=");
        paramMap.put("nonceStr", "123456");
        paramMap.put("applyNo", "ZZ1907042123010158");
        paramMap.put("sign", "E1F7514CA9938E83D58D9582E9DC460A");

        String paramJson = JSONObject.toJSONString(paramMap);

        Map<String, String> headMap = new HashMap<>(1);
        headMap.put("Content-Type", "application/json");

        try {
            String response = HttpClient.getInstance().sendHttpPost("http://hljzxxd.loan.test.hljzxxd.com/gateway/openapi/consume/order/getOrderInfoDetail", paramJson, headMap);
            JSONObject responseJson = JSONObject.parseObject(response);
            String code = responseJson.getString("code");
            if (StringUtils.equals(code, "200")) {
                codeList.add(count + "");
            }
            System.out.println("code for size = " + codeList.size());

        } catch (Exception e) {
            throw new ServiceException(-1, e);
        }
    }
}






