package cn.m1yellow.mypages.common.constant;

/**
 * 权限相关常量定义
 * <br>
 * 第一次见接口定义常量？留着多见见，哈哈哈
 * Created by macro on 2020/6/19.
 */
public interface AuthConstant {

    /**
     * JWT存储权限前缀
     */
    String AUTHORITY_PREFIX = "ROLE_";

    /**
     * JWT存储权限属性字段
     */
    String AUTHORITY_CLAIM_NAME = "authorities";

    /**
     * 单页应用 client_id
     */
    String ADMIN_CLIENT_ID = "admin-web";

    /**
     * 授权服务登录密码
     */
    String ADMIN_CLIENT_PWD = "123456";

    /**
     * 门户首页 client_id
     */
    String PORTAL_CLIENT_ID = "portal-web";

    /**
     * 后台管理接口路径匹配
     */
    String ADMIN_URL_PATTERN = "/mypages-admin/**";

    /**
     * Redis缓存权限规则key
     */
    String RESOURCE_ROLES_MAP_KEY = "auth:resourceRolesMap";

    /**
     * 授权方式，密码模式
     */
    String GRANT_TYPE_PASSWORD = "password";

    /**
     * 授权方式，刷新 token
     */
    String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    /**
     * 认证信息Http请求头
     */
    String JWT_TOKEN_HEADER = "Authorization";

    /**
     * JWT令牌前缀
     */
    String JWT_TOKEN_PREFIX = "Bearer ";

    /**
     * 用户信息Http请求头
     */
    String USER_TOKEN_HEADER = "user";

}
