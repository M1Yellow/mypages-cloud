package cn.m1yellow.mypages.auth.config;

import cn.m1yellow.mypages.auth.component.JwtTokenEnhancer;
import cn.m1yellow.mypages.auth.entity.SysPermission;
import cn.m1yellow.mypages.auth.entity.SysRole;
import cn.m1yellow.mypages.auth.entity.SysRolePermission;
import cn.m1yellow.mypages.auth.service.SysConfigService;
import cn.m1yellow.mypages.auth.service.SysPermissionService;
import cn.m1yellow.mypages.auth.service.SysRolePermissionService;
import cn.m1yellow.mypages.auth.service.SysRoleService;
import cn.m1yellow.mypages.auth.service.impl.UserDetailsServiceImpl;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.util.FastJsonUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.common.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.util.*;

/**
 * 认证服务器配置
 * <br>
 * OAuth2中的四种认证授权模式
 * 授权码模式（authorization code）
 * 简化模式/隐式授权模式（implicit）
 * 密码模式（password）
 * 客户端模式（client credentials）
 */
@Configuration
@EnableAuthorizationServer
public class Oauth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenEnhancer jwtTokenEnhancer;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private SysRolePermissionService rolePermissionService;
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 资源路径对应的角色列表
     */
    private Map<String, List<String>> resourceRolesMap;
    /**
     * 数据库参数配置
     */
    private Map<String, String> sysConfigMap;


    @PostConstruct
    public void initData() {
        // 初始化资源路径对应的角色列表
        resourceRolesMap = this.getResourceRolesMap();
        // 加载数据库配置
        this.sysConfigMap = this.getSysConfigMap();
    }

    private Map<String, List<String>> getResourceRolesMap() {
        Map<String, List<String>> resourceRolesMap = new TreeMap<>();
        // 先从缓存中获取
        String resourceRolesMapStr = ObjectUtil.getString(redisUtil.get(GlobalConstant.RESOURCE_ROLES_MAP_KEY));
        if (StringUtils.isNotBlank(resourceRolesMapStr)) {
            resourceRolesMap = FastJsonUtil.json2Bean(resourceRolesMapStr, resourceRolesMap.getClass());
        }
        if (resourceRolesMap != null && resourceRolesMap.size() > 0) {
            return resourceRolesMap;
        }
        if (resourceRolesMap == null) resourceRolesMap = new TreeMap<>();
        // 获取数据库中所有 url
        List<SysPermission> permissionList = permissionService.queryAllPermission();
        for (SysPermission permission : permissionList) {
            // 获取该 url 所对应的角色权限 role_permission，可能有多个
            Map<String, Object> params = new HashMap<>();
            params.put("permission_id", permission.getId());
            List<SysRolePermission> rolePermissionList = rolePermissionService.listByMap(params);
            List<String> roles = new ArrayList<>(); // 直接 new 吧，别拆开判断 if null 了，容易出错
            if (rolePermissionList != null && rolePermissionList.size() > 0) {
                for (SysRolePermission rolePermission : rolePermissionList) {
                    // 由角色权限查角色
                    Long roleId = rolePermission.getRoleId();
                    if (roleId != null) {
                        SysRole role = roleService.getById(roleId);
                        if (role != null)
                            roles.add(role.getCode()); // code like admin
                    }
                }
            }
            if (roles != null && roles.size() > 0) {
                resourceRolesMap.put(permission.getUrl(), roles);
            }
        }

        if (resourceRolesMap != null && resourceRolesMap.size() > 0) {
            //redisTemplate.opsForHash().putAll(RedisConstant.RESOURCE_ROLES_MAP, resourceRolesMap);
            redisUtil.set(GlobalConstant.RESOURCE_ROLES_MAP_KEY, FastJsonUtil.bean2Json(resourceRolesMap));
        }

        return resourceRolesMap;
    }


    private Map<String, String> getSysConfigMap() {
        Map<String, String> sysConfigMap = new HashMap<>();
        /*
        sysConfigMap.put("oauth_client_id", "admin-web");
        sysConfigMap.put("oauth_client_secret", "123456");
        sysConfigMap.put("oauth_scopes", "all");
        sysConfigMap.put("oauth_grant_types", "password,refresh_token");
        sysConfigMap.put("oauth_access_token_validity_seconds", "7200");
        sysConfigMap.put("oauth_refresh_token_validity_seconds", "86400");

        sysConfigMap.put("jwt_key_alias", "jwt");
        sysConfigMap.put("jwt_key_password", "123456");
        */

        sysConfigMap = sysConfigService.getSysConfigs();

        return sysConfigMap;
    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /*
        clients.inMemory()
                // 授权服务器的账号密码，这里的授权服务器就是自己搭建的，账号密码自己设置就行了，可以设置多套账户密码
                // appid
                .withClient("admin-web")
                // appsecret
                .secret(passwordEncoder.encode("123456"))
                // 作用域范围
                .scopes("all")
                // 授权模式-密码模式，适用于自家内部系统
                .authorizedGrantTypes("password,refresh_token")
                .accessTokenValiditySeconds(7200) // 两小时
                .refreshTokenValiditySeconds(86400); // 一天
        */

        // 读取数据库参数配置
        String clientId = ObjectUtil.getString(sysConfigMap.get("oauth_client_id"));
        String clientSecret = ObjectUtil.getString(sysConfigMap.get("oauth_client_secret"));
        String scopes = ObjectUtil.getString(sysConfigMap.get("oauth_scopes"));
        String grantTypeStr = ObjectUtil.getString(sysConfigMap.get("oauth_grant_types"));
        String accessTokenValiditySecondsStr = ObjectUtil.getString(sysConfigMap.get("oauth_access_token_validity_seconds"));
        String refreshTokenValiditySecondsStr = ObjectUtil.getString(sysConfigMap.get("oauth_refresh_token_validity_seconds"));

        String[] grantTypes = grantTypeStr.split(",");
        int accessTokenValiditySeconds = Integer.parseInt(accessTokenValiditySecondsStr);
        int refreshTokenValiditySeconds = Integer.parseInt(refreshTokenValiditySecondsStr);

        clients.inMemory()
                // 授权服务器的账号密码，这里的授权服务器就是自己搭建的，账号密码自己设置就行了，可以设置多套账户密码
                // appid
                .withClient(clientId)
                // appsecret
                // TODO 注意，授权服务端接收到明文密码后，会进行加密。先在前端/客户端加密后再传到服务端，服务端还是会再加密，导致最后校验失败
                //  有没有办法，前端加密后传到服务端，服务端不再加密，直接校验？这样能避免密码明文传输
                .secret(passwordEncoder.encode(clientSecret))
                // 作用域范围
                .scopes(scopes)
                // 授权模式-密码模式，适用于自家内部系统
                .authorizedGrantTypes(grantTypes)
                .accessTokenValiditySeconds(accessTokenValiditySeconds) // 两小时
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds); // 一天

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(jwtTokenEnhancer);
        delegates.add(accessTokenConverter());
        enhancerChain.setTokenEnhancers(delegates); // 配置JWT的内容增强器
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService) // 配置加载用户信息的服务
                .accessTokenConverter(accessTokenConverter())
                .tokenEnhancer(enhancerChain);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients();
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        /*
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
        */

        // 读取数据库参数配置
        // 生成 jwt RSA key 时设置的别名
        String jwtKeyAlias = ObjectUtil.getString(sysConfigMap.get("jwt_key_alias"));
        // 生成 jwt RSA key 时设置的密码
        String jwtKeyPassword = ObjectUtil.getString(sysConfigMap.get("jwt_key_password"));

        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), jwtKeyPassword.toCharArray());
        return keyStoreKeyFactory.getKeyPair(jwtKeyAlias, jwtKeyPassword.toCharArray());
    }

}
