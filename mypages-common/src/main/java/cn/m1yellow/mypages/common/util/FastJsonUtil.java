package cn.m1yellow.mypages.common.util;

import com.alibaba.fastjson.JSON;

import java.util.List;

public class FastJsonUtil {

    public static String bean2Json(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T json2Bean(String jsonStr, Class<T> clazz) {
        return JSON.parseObject(jsonStr, clazz);
    }

    public static <T> List<T> json2List(String jsonStr, Class<T> clazz) {
        return JSON.parseArray(jsonStr, clazz);
    }

}
