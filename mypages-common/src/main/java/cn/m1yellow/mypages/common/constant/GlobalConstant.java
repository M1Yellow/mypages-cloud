package cn.m1yellow.mypages.common.constant;

/**
 * 全局常量配置类
 */
public class GlobalConstant {

    //==============================================================================//
    // 项目业务相关常量配置
    //==============================================================================//
    /** 默认当前页 */
    public static final int PAGE_NO_DEFAULT = 1;
    /** 默认每页记录条数 */
    public static final int PAGE_SIZE_DEFAULT = 10;


    //==============================================================================//
    // 缓存相关常量配置
    //==============================================================================//
    /**
     * 用户登录时，平台首页数据缓存
     */
    public static final String HOME_PLATFORM_LIST_CACHE_KEY = "HOME_PLATFORM_LIST_CACHE_";

    /**
     * 用户未登录时，默认平台首页数据缓存
     */
    public static final String HOME_PLATFORM_LIST_DEFAULT_CACHE_KEY = "HOME_PLATFORM_LIST_DEFAULT_CACHE";

    /**
     * 平台列表基础数据
     */
    public static final String PLATFORM_LIST_BASE_CACHE_KEY = "USER_PLATFORM_LIST_BASE_CACHE";

    /**
     * 用户添加的平台列表
     */
    public static final String USER_PLATFORM_LIST_CACHE_KEY = "USER_PLATFORM_LIST_CACHE_";

    /**
     * 用户未添加的平台基础列表，用于添加平台时，做下拉选择
     */
    public static final String USER_PLATFORM_LIST_ADD_CACHE_KEY = "USER_PLATFORM_LIST_ADD_CACHE_";

    /**
     * 用户添加的类型列表
     */
    public static final String USER_TYPE_LIST_CACHE_KEY = "USER_TYPE_LIST_CACHE_";

    /**
     * 首页内容缓存时间，单位：秒
     */
    public static final long HOME_PLATFORM_LIST_CACHE_TIME = 2 * 60 * 60;

    /**
     * 用户平台列表、分类类型列表缓存时间，单位：秒
     */
    public static final long USER_PLATFORM_TYPE_LIST_CACHE_TIME = 10 * 60;

    /** 系统配置项 key-value 缓存，默认不过期，新增或修改配置的时候，清空缓存，用的时候会重新加载 */
    public static final String SYS_CONFIG_MAP_CACHE_KEY = "SYS_CONFIG_MAP_CACHE";

    /** 用户详细信息缓存 */
    public static final String USER_INFO_DETAIL_CACHE_KEY = "USER_INFO_DETAIL_CACHE_";

    /** 关注用户分页数据列表缓存 */
    public static final String USER_FOLLOWING_PAGE_LIST_CACHE_KEY = "USER_FOLLOWING_PAGE_LIST_CACHE_";
    /** 关注用户分页数据列表缓存时间，单位：秒 */
    public static final long USER_FOLLOWING_PAGE_LIST_CACHE_TIME = 60 * 60;


    //==============================================================================//
    // 数据库基础数据常量
    //==============================================================================//
    /**
     * 默认关注用户分类名称
     */
    public static final String USER_FOLLOWING_DEFAULT_TYPE_NAME = "默认分类";
    /** 默认关注用户分类id，游离于数据库表之外，便于各平台、各类型共用 */
    public static final Long USER_FOLLOWING_DEFAULT_TYPE_ID = 0L;


    //==============================================================================//
    // 用户数据相关常量配置
    //==============================================================================//
    /**
     * 排序优先级，1~10 优先级递增
     */
    public static final int[] SORT_VALUES = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    /**
     * 用户未登录时，默认取哪个用户的数据
     */
    public static final long HOME_PLATFORM_LIST_DEFAULT_USER_ID = 1L;
    /**
     * 用户未登录时，默认显示多少个平台
     */
    public static final int HOME_PLATFORM_LIST_DEFAULT_PLATFORM_NUM = 3;
    /**
     * 用户未登录时，默认每个平台或类型取多少条观点数据
     */
    public static final int HOME_PLATFORM_LIST_DEFAULT_OPINION_NUM = 3;
    /**
     * 用户未登录时，默认每个平台或类型取多少个关注用户数据
     */
    public static final int HOME_PLATFORM_LIST_DEFAULT_FOLLOWING_NUM = 5;
    /**
     * 限制同一平台、同一类型下的观点数量
     */
    public static final int PLATFORM_TYPE_OPINION_NUM = 10;
    /**
     * 限制用户对同一个关注用户添加标签的数量
     */
    public static final int SAME_USER_FOLLOWING_REMARK_NUM = 10;
    /**
     * 用户默认头像 profile_photo 路径
     */
    public static final String USER_DEFAULT_PROFILE_PHOTO_PATH = "/images/user-profile-photo/f0ee8a3c7c9638a54940382568c9dpng.png";


    //==============================================================================//
    // spring security 权限校验
    //==============================================================================//
    /**
     * 静态资源请求
     */
    public static final String[] STATIC_RESOURCES = {
            // 静态资源
            "/**/images/**"
            , "/**/css/**"
            , "/**/js/**"
            , "/**/*.css"
            , "/**/*.js"
            , "/**/*.png"
            , "/**/*.jpg"
            , "/**/*.jpeg"
            , "/**/*.gif"
            , "/**/*.ico"
    };

    /**
     * 需要登录
     */
    public static final String NEED_LOGIN = "needLogin";

    /**
     * 资源路径未找到
     */
    public static final String URI_NOT_FOUND = "uriNotFound";

    /**
     * 路径权限表所有权限缓存
     */
    public static final String SYS_PERMISSION_LIST_CACHE_KEY = "SYS_PERMISSION_LIST_CACHE";
    /**
     * 用户角色关联列表缓存
     */
    public static final String USER_ROLE_RELATION_LIST_CACHE_KEY = "USER_ROLE_RELATION_LIST_CACHE_";
    /**
     * 用户拥有的角色列表缓存
     */
    public static final String USER_ROLE_LIST_CACHE_KEY = "USER_ROLE_LIST_CACHE_";


}
