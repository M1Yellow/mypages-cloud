package cn.m1yellow.mypages.common.constant;

/**
 * 全局常量配置类
 */
public class GlobalConstant {

    //==============================================================================//
    // 缓存相关常量配置
    //==============================================================================//

    /** 用户登录时，平台首页数据缓存 */
    public static final String HOME_PLATFORM_LIST_CACHE_KEY = "HOME_PLATFORM_LIST_CACHE_";

    /** 用户未登录时，默认平台首页数据缓存 */
    public static final String HOME_PLATFORM_LIST_DEFAULT_CACHE_KEY = "HOME_PLATFORM_LIST_DEFAULT_CACHE";
    /** 用户未登录时，默认取哪个用户的数据 */
    public static final long HOME_PLATFORM_LIST_DEFAULT_USER_ID = 1L;
    /** 用户未登录时，默认每个平台或类型取多少条观点数据 */
    public static final int HOME_PLATFORM_LIST_DEFAULT_OPINION_NUM = 3;
    /** 用户未登录时，默认每个平台或类型取多少个关注用户数据 */
    public static final int HOME_PLATFORM_LIST_DEFAULT_FOLLOWING_NUM = 5;

    /** 平台列表基础数据 */
    public static final String PLATFORM_LIST_BASE_CACHE_KEY = "USER_PLATFORM_LIST_BASE_CACHE";

    /** 用户添加的平台列表 */
    public static final String USER_PLATFORM_LIST_CACHE_KEY = "USER_PLATFORM_LIST_CACHE_";

    /** 用户未添加的平台基础列表，用于添加平台时，做下拉选择 */
    public static final String USER_PLATFORM_LIST_ADD_CACHE_KEY = "USER_PLATFORM_LIST_ADD_CACHE_";

    /** 用户添加的类型列表 */
    public static final String USER_TYPE_LIST_CACHE_KEY = "USER_TYPE_LIST_CACHE_";

    /** 首页内容缓存时间，单位：秒 */
    public static final long HOME_PLATFORM_LIST_CACHE_TIME = 2 * 60 * 60;
    /** 用户平台列表、分类类型列表缓存时间，单位：秒 */
    public static final long USER_PLATFORM_TYPE_LIST_CACHE_TIME = 10 * 60;


    //==============================================================================//
    // 用户数据相关常量配置
    //==============================================================================//

    /** 排序优先级，1~10 优先级递增 */
    public static final int[] SORT_VALUES = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};


    //==============================================================================//
    // spring security 权限校验
    //==============================================================================//

    /** 需要登录 */
    public static final String NEED_LOGIN = "needLogin";

    /** 资源路径未找到 */
    public static final String URI_NOT_FOUND = "uriNotFound";

    /** 路径权限表所有权限缓存 */
    public static final String SYS_PERMISSION_LIST_CACHE_KEY = "SYS_PERMISSION_LIST_CACHE";
    /** 用户角色关联列表缓存 */
    public static final String USER_ROLE_RELATION_LIST_CACHE_KEY = "USER_ROLE_RELATION_LIST_CACHE_";
    /** 用户拥有的角色列表缓存 */
    public static final String USER_ROLE_LIST_CACHE_KEY = "USER_ROLE_LIST_CACHE_";


}
