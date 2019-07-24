package com.treefintech.b2b.oncecenter.common.utils;

import com.treefinance.b2b.common.utils.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author shiyc
 * @date 2019/1/22 11:51
 */
public class MapUtils extends org.apache.commons.collections.MapUtils {

    public static String join(Map value, String[] excludeKeys, String keySeparator, String pairSeparator) {
        if (isEmpty(value)) {
            return null;
        }
        if (StringUtils.isEmpty(keySeparator)) {
            keySeparator = "";
        }
        if (StringUtils.isEmpty(pairSeparator)) {
            pairSeparator = "";
        }
        if (excludeKeys == null) {
            excludeKeys = new String[]{};
        }
        List excludeKeyList = Arrays.asList(excludeKeys);

        List<String> result = new ArrayList<>(value.size());
        for (Object o : value.keySet()) {
            if (!excludeKeyList.contains(o)) {
                result.add(o + keySeparator + value.get(o));
            }
        }
        return StringUtils.join(result, pairSeparator);
    }


}
