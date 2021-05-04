package com.m1yellow.mypages.common.constant;

/**
 * 全局常量配置类
 */
public class GlobalConstant {

    public static final String HOME_PLATFORM_LIST_CACHE_KEY = "HOME_PLATFORM_LIST_CACHE_";
    public static final String HOME_PLATFORM_LIST_DEFAULT_CACHE_KEY = "HOME_PLATFORM_LIST_DEFAULT_CACHE";

    public static final String USER_PLATFORM_LIST_CACHE_KEY = "USER_PLATFORM_LIST_CACHE_";
    public static final String USER_TYPE_LIST_CACHE_KEY = "USER_TYPE_LIST_CACHE_";

    /**
     * 首页内容缓存时间，单位：秒
     */
    public static final long HOME_PLATFORM_LIST_CACHE_TIME = 2 * 60 * 60;
    /**
     * 用户平台列表、分类类型列表缓存时间，单位：秒
     */
    public static final long USER_PLATFORM_TYPE_LIST_CACHE_TIME = 10 * 60;

}
