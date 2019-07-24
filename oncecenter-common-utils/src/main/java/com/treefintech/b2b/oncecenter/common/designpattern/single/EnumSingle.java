package com.treefintech.b2b.oncecenter.common.designpattern.single;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用枚举实现单例
 *
 * @author zhangfan
 * @description
 * @date 2019/7/9 上午11:12
 **/
public enum EnumSingle {

    INSTANCE;

    private Map<String, String> map;

    EnumSingle() {
        map = new HashMap<>();
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void putMap(String key, String value) {
        map.put(key, value);
    }
}
