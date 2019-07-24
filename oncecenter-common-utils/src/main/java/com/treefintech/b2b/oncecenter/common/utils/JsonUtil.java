package com.treefintech.b2b.oncecenter.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangfan
 * @description
 * @date 2019/5/31 下午4:22
 **/
public abstract class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(com.treefinance.b2b.common.utils.JsonUtil.class);


    public static String jsonFromObject(Object object) {
        if (object instanceof String) {
            return object.toString();
        }
        return JSON.toJSONString(object);
    }

    @SuppressWarnings("deprecation")
    public static <T> T objectFromJson(String json, Class<T> klass) {
        return JSON.parseObject(json, klass);
    }

    public static Object objectFromJson(String json, String clazzName) {
        if (StringUtils.isEmpty(clazzName)) {
            return json;
        }
        try {
            Class clazz = Class.forName(clazzName);
            if (clazz.isAssignableFrom(String.class)) {
                return json;
            }
            return com.treefinance.b2b.common.utils.JsonUtil.objectFromJson(json, clazz);
        } catch (Exception e) {
            log.error("转换错误" + e.getMessage());
        }
        return json;
    }


    @SuppressWarnings("rawtypes")
    public static Object getValue(Map map, String... keys) {
        if (map == null) {
            return null;
        }
        for (int i = 0; i < keys.length - 1; i++) {
            if (!map.containsKey(keys[i]) || map.get(keys[i]) == null) {
                return null;
            }
            map = (Map) map.get(keys[i]);
        }

        if (!map.containsKey(keys[keys.length - 1]) || map.get(keys[keys.length - 1]) == null) {
            return null;
        }
        return map.get(keys[keys.length - 1]);
    }

    @SuppressWarnings("rawtypes")
    public static String getString(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            return String.valueOf(obj);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Map getMap(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            {
                return null;
            }
        } else {
            return (Map) obj;
        }
    }



    @SuppressWarnings("rawtypes")
    public static Integer getInteger(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            return Integer.valueOf(String.valueOf(obj));
        }
    }

    @SuppressWarnings("rawtypes")
    public static Long getLong(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            return Long.valueOf(String.valueOf(obj));
        }
    }

    @SuppressWarnings("rawtypes")
    public static BigDecimal getBigDecimal(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            return new BigDecimal(obj + "");
        }
    }

    @SuppressWarnings("rawtypes")
    public static List getList(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            return (List) obj;
        }
    }

    static final Pattern RegEx_Double = Pattern.compile("([0-9.]+)");

    @SuppressWarnings("rawtypes")
    public static Double getDouble(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            Matcher m = RegEx_Double.matcher(String.valueOf(obj));
            if (m.find()) {
                return Double.valueOf(m.group(1));
            } else {
                return null;
            }
        }
    }

    @SuppressWarnings({"rawtypes"})
    public static Map toMap(String jsonString) throws JSONException {


        JSONObject jsonObject = JSON.parseObject(jsonString);
        ;

        return toMap(jsonObject);
    }

    @SuppressWarnings({"rawtypes"})
    public static List toList(String jsonString) throws JSONException {
        JSONArray jsonObject = JSON.parseArray(jsonString);
        return toList(jsonObject);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map toMap(JSONObject jsonObject) {
        Map result = new HashMap();
        Iterator iterator = jsonObject.keySet().iterator();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }
            result.put(key, value);

        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List toList(JSONArray array) {
        List result = new ArrayList();
        for (Object value : array) {
            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }
            result.add(value);
        }
        return result;
    }


    /**
     * 判断json字符串是否有效
     *
     * @param json json字符串
     * @return
     */
    public static boolean isJSONValid(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        }
        try {
            JSON.parseObject(json);
            return true;
        } catch (JSONException e) {
            try {
                JSON.parseArray(json);
                return true;
            } catch (JSONException e1) {
                return false;
            }
        }
    }


    /**
     * 判断json是否为空值
     *
     * @param json json字符串
     * @return true if empty
     */
    public static boolean isEmpty(String json) {
        return StringUtils.isBlank(json) || "{}".equals(json) || "[]".equals(json);
    }
}
