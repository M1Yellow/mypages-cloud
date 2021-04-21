package com.m1yellow.mypages.common.util;

public class ObjectUtil {
    public static String getString(Object o) {
        if (o == null) return null;
        return o.toString();
    }
}
