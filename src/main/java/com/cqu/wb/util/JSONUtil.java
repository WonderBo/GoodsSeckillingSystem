package com.cqu.wb.util;

import com.alibaba.fastjson.JSON;

/**
 * Created by jingquan on 2018/8/5.
 */
public class JSONUtil {

    /**
     *
     * @param str
     * @param clazz
     * @param <T>
     * @return
     * @description 将String类型转化为对象类型
     */
    public static <T> T stringToBean(String str, Class<T> clazz) {
        // 基础类型及其封装类型判断（参数校验）
        if(clazz == int.class || clazz == Integer.class) {
            return (T)Integer.valueOf(str);
        } else if(clazz == long.class || clazz == Long.class) {
            return (T)Long.valueOf(str);
        } else if(clazz == String.class) {
            return (T)str;
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    /**
     *
     * @param value
     * @param <T>
     * @return
     * @description 将对象类型转化为String类型
     */
    public static <T> String beanTOString(T value) {
        // 基础类型及其封装类型判断（参数校验）
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if(clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else if(clazz == String.class) {
            return (String)value;
        } else {
            return JSON.toJSONString(value);
        }
    }
}
