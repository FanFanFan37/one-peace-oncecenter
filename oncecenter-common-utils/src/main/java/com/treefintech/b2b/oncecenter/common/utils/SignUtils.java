package com.treefintech.b2b.oncecenter.common.utils;

import com.treefinance.b2b.common.utils.JsonUtil;
import com.treefinance.b2b.common.utils.MapUtils;
import com.treefinance.b2b.common.utils.encrypt.MD5Util;
import com.treefinance.b2b.common.utils.lang.NumberUtils;
import com.treefinance.b2b.common.utils.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class SignUtils {

    public static void main(String[] args) {
        BigDecimal borrowAmount = BigDecimal.valueOf(10000);
        BigDecimal monthRate = BigDecimal.valueOf(0.36/12);
        System.out.println("monthRate = " + monthRate);
        Integer periodValue = 6;

        Integer periodIndex = 4;


        Double monthRatePlusPeriod = Math.pow(NumberUtils.add(BigDecimal.ONE, monthRate).doubleValue(), periodValue.doubleValue());

        BigDecimal shouldRepayAmount = NumberUtils.multiply(borrowAmount, monthRate, BigDecimal.valueOf(monthRatePlusPeriod)).divide(BigDecimal.valueOf(monthRatePlusPeriod - 1), 30, RoundingMode.DOWN);
        BigDecimal shouldRepayInterest = borrowAmount.multiply(monthRate).multiply(BigDecimal.valueOf(monthRatePlusPeriod - getSubOne(periodIndex, monthRate))).divide(BigDecimal.valueOf(monthRatePlusPeriod - 1), 30, RoundingMode.DOWN);

        BigDecimal shouldRepayPrincipal = borrowAmount.multiply(monthRate).multiply(BigDecimal.valueOf(getSubOne(periodIndex, monthRate))).divide(BigDecimal.valueOf(monthRatePlusPeriod - 1), 30, RoundingMode.DOWN);

        BigDecimal subAmount = shouldRepayAmount.subtract(shouldRepayInterest).subtract(shouldRepayPrincipal);

        System.out.println("shouldRepayAmount = " + shouldRepayAmount);
        System.out.println("shouldRepayInterest = " + shouldRepayInterest);
        System.out.println("shouldRepayPrincipal = " + shouldRepayPrincipal);
        System.out.println("subAmount = " + subAmount);
    }

    private static Double getSubOne(Integer periodIndex, BigDecimal monthRate) {
        return Math.pow(NumberUtils.add(BigDecimal.ONE, monthRate).doubleValue(), periodIndex - 1);
    }

//    public static void main(String[] args) {
//        String jsonStr = "\"accessToken\":\"UZZCOQ+bo4xO4UiRCVBRJugixpCJXhTM6vE+MkPjPiU=\",\"endTime\":\"15595776000\",\"merchantCode\":\"BJZZJF190523001\",\"nonceStr\":\"113036\",\"openid\":\"DF33FE1D0B819099C9ADECEB4B25A90A\",\"productCode\":\"ZZJF001\",\"startTime\":\"15595776000\"}";
//        //,"sign":"40DF1264A8403A93222CC5DA62173A48"
//        String jsonStrA = "{" + jsonStr;
//
//        LinkedHashMap<String, Object> requestMap = JSON.parseObject(jsonStrA, LinkedHashMap.class, Feature.OrderedField);
//        String requestSign = getSignToken(requestMap, "0B2AD18C683179C325FB2B133BA044D0");
//        System.out.println("requestSign = " + requestSign);
//
//        String request = "{\"sign\":\"" + requestSign + "\"," + jsonStr;
//        System.out.println(request);
//
////        request = "{\"merchantCode\":\"BJZZJF190523001\",\"applyAccountRequest\":{\"acUsCd\":\"FK\",\"accountName\":\"刘欣\",\"accountType\":\"4\",\"accountNo\":\"6222020402013987480\",\"bankNo\":\"CCB\",\"mobile\":\"15831161907\",\"papersTypeCode\":\"SF\",\"papersNo\":\"140203199712174343\",\"acCuCd\":\"RMB\",\"issuer\":\"105100031049\",\"acCd\":\"JS\"},\"openid\":\"A952DED4B8B33EEBAB20E691117D0E02\",\"contractNo\":\"ZZ1906041248540008\",\"attachmentUsedList\":[{\"attachmentCode\":\"idcardFile\",\"attachmentName\":\"身份证\",\"attachmentId\":262577666589345051},{\"attachmentCode\":\"idcardFile\",\"attachmentName\":\"身份证\",\"attachmentId\":262577666589345052}],\"sign\":\"19CCD784CE33A6395599506AA79A5155\",\"accessToken\":\"tWS7NXBHLugTKe+IN0NZF+xQ5l2NzC4z7r/VdwkBFKA=\",\"applyPeopleRequest\":{\"isAllowCredit\":\"N\",\"customerTypeCode\":\"YB\",\"mobile\":\"15831161907\",\"merchantProductUserId\":\"140203199712174343\",\"papersTypeCode\":\"SF\",\"papersNo\":\"140203199712174343\",\"customerName\":\"刘欣\",\"applyPepleTypeCode\":\"ZS\"},\"nonceStr\":\"539178\",\"applyAmountRequest\":{\"rateValue\":16.50,\"orderType\":\"ApplyBorrow\",\"extendParams\":{\"riskDataMap\":{\"risktest\":\"riskDataMapTest\"}},\"borrowAmount\":1000.55,\"productCode\":\"ZZJF002\",\"repaymentType\":\"ZhangZhongOnce\",\"applyNo\":\"ZZ1906041248540008\",\"rateUnit\":\"YEAR\",\"currency\":\"RMB\",\"overdueGraceOffset\":30,\"periodUnit\":\"DAY\",\"periodValue\":50},\"productCode\":\"ZZJF002\",\"applyNo\":\"ZZ1906041248540008\",\"grantType\":\"grantType\"}";
//
//        Map<String, String> headMap = new HashMap<>(1);
//        headMap.put("Content-Type", "application/json");
//        try {
//            String response = HttpClient.getInstance().sendHttpPost("http://localhost:8190/openapi/consume/debtinfo/queryPayReconciliationList", request, headMap);
//            System.out.println(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



    /**
     * 生成令牌签名
     */
    @SuppressWarnings("unchecked")
    public static String getSignToken(Map<String, Object> map, String merchantKey) {
        List<String> excudeKeyList = new ArrayList<>();
        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            if (stringObjectEntry.getValue() == null) {
                excudeKeyList.add(stringObjectEntry.getKey());
            }
        }
        excudeKeyList.add("sign");

        return signUpperCase(map, excudeKeyList.toArray(new String[excudeKeyList.size()]), "key=" + merchantKey, null, null, "=", "&");
    }

    public static <T> String signUpperCase(T value, String[] excudeKeys, String suffix) {
        return signUpperCase(value, excudeKeys, suffix, null, null);
    }

    public static <T> String signUpperCase(T value, String[] excudeKeys, String suffix, String prefix, String characterEncoding) {
        return signUpperCase(value, excudeKeys, suffix, prefix, characterEncoding, "=", "&");
    }


    public static <T> String signUpperCase(T value, String[] excudeKeys, String suffix, String prefix, String characterEncoding, String keySeparator, String pairSeparator) {
        String sign = sign(value, excudeKeys, suffix, prefix, characterEncoding, keySeparator, pairSeparator);
        return StringUtils.upperCase(sign);
    }

    public static <T> String sign(T value, String[] excudeKeys, String suffix) {
        return sign(value, excudeKeys, suffix, null, null);
    }

    public static <T> String sign(T value, String[] excudeKeys, String suffix, String prefix, String characterEncoding) {
        return sign(value, excudeKeys, suffix, prefix, characterEncoding, "=", "&");
    }

    //生成签名
    @SuppressWarnings("unchecked")
    public static <T> String sign(T value, String[] excudeKeys, String suffix, String prefix, String characterEncoding, String keySeparator, String pairSeparator) {
        SortedMap sortedMap = new TreeMap();
        if (value instanceof Map) {
            sortedMap.putAll((Map) value);
        } else {
            sortedMap.putAll(JsonUtil.toMap(JsonUtil.jsonFromObject(value)));
        }

        if (StringUtils.isEmpty(keySeparator)) {
            keySeparator = "";
        }
        if (StringUtils.isEmpty(pairSeparator)) {
            pairSeparator = "";
        }

        String tmpStr = MapUtils.join(sortedMap, excudeKeys, keySeparator, pairSeparator);
        if (StringUtils.isNotEmpty(suffix)) {
            tmpStr += pairSeparator + suffix;
        }

        if (StringUtils.isNotEmpty(prefix)) {
            tmpStr = prefix + pairSeparator + tmpStr;
        }
        System.out.println("sign for MD5 tmpStr = " + tmpStr);
        return MD5Util.getInstance().getStringMD5(tmpStr, characterEncoding);
    }

}

