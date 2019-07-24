package com.treefintech.b2b.oncecenter.common.utils;

import com.treefinance.b2b.common.utils.lang.StringUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Object Bean Utils
 */
public class BeanUtil {

    /**
     * Map转objectBean
     * @param clazz
     * @param map
     * @param <T>
     * @return
     */
    public static <T> T convertMap2ObjectBean(Class<T> clazz, Map<String, Object> map) {
        try {
            T target = clazz.newInstance();
            if (map == null) {
                return target;
            }

            List<Field> fieldList = getAccessibleFieldList(clazz);
            for (Field field : fieldList) {
                Object objectValue = map.get(field.getName());
                if (objectValue != null) {
                    if (String.class.equals(field.getType())) {
                        objectValue = String.valueOf(objectValue);
                    } else if (!field.getType().isAssignableFrom(objectValue.getClass())) {

                        if (StringUtils.isNotBlank(String.valueOf(objectValue))) {
                            objectValue = ConvertUtils.convert(objectValue, field.getType());
                        } else {
                            objectValue = null;
                        }
                    }
                }
                field.set(target, objectValue);
            }
            return target;
        } catch (Exception e) {
            throw new RuntimeException("map convert2 " + clazz.getName() + " is error , errorMsg = " + e.getMessage());
        }
    }


    /**
     * 获取clazz中的所有属性, 包括父类
     * @param clazz
     * @return
     */
    private static List<Field> getAccessibleFieldList(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        for (Class<?> superClass = clazz; superClass != Object.class ; superClass = superClass.getSuperclass()) {
            for (Field field : superClass.getDeclaredFields()) {

                Boolean inInSubClass = Boolean.FALSE;
                for (Field hasExistField : fieldList) {
                    if (StringUtils.equals(hasExistField.getName(), field.getName())) {
                        inInSubClass = Boolean.TRUE;
                        break;
                    }
                }

                if (!inInSubClass) {
                    ReflectionUtils.makeAccessible(field);
                    fieldList.add(field);
                }
            }
        }
        return fieldList;
    }

}







