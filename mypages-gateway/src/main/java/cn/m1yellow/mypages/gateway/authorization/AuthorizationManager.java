package cn.m1yellow.mypages.gateway.authorization;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import cn.m1yellow.mypages.common.constant.AuthConstant;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.domian.JwtPayloadDto;
import cn.m1yellow.mypages.common.util.FastJsonUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.common.util.RedisUtil;
import cn.m1yellow.mypages.gateway.config.IgnoreUrlsConfig;
import com.nimbusds.jose.JWSObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 鉴权管理器，用于判断是否有资源的访问权限
 * Created by macro on 2020/6/19.
 */
@Slf4j
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;


    /**
     * 资源路径对应的角色列表
     */
    private Map<String, List<String>> resourceRolesMap;


    @PostConstruct
    public void initData() {
        // TODO 依赖 auth 项目，初始化资源路径对应的角色列表
        resourceRolesMap = this.getResourceRolesMap();
    }

    private Map<String, List<String>> getResourceRolesMap() {
        Map<String, List<String>> resourceRolesMap = new TreeMap<>();
        // 先从缓存中获取
        String resourceRolesMapStr = ObjectUtil.getString(redisUtil.get(GlobalConstant.RESOURCE_ROLES_MAP_KEY));
        if (StringUtils.isNotBlank(resourceRolesMapStr)) {
            resourceRolesMap = FastJsonUtil.json2Bean(resourceRolesMapStr, resourceRolesMap.getClass());
        }

        return resourceRolesMap;
    }


    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {

        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        URI uri = request.getURI();
        String path = uri.getPath();
        // 微服务路径不参与权限验证
        path = this.StripPathPrefix(path, 1);
        PathMatcher pathMatcher = new AntPathMatcher();
        // 白名单路径直接放行
        List<String> ignoreUrls = ignoreUrlsConfig.getUrls();
        for (String ignoreUrl : ignoreUrls) {
            if (pathMatcher.match(ignoreUrl, path)) {
                return Mono.just(new AuthorizationDecision(true));
            }
        }
        // 对应跨域的预检请求直接放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }
        // 不同用户体系登录不允许互相访问
        try {
            String token = request.getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
            if (StringUtils.isBlank(token)) {
                return Mono.just(new AuthorizationDecision(false));
            }
            String realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String jwtPayloadStr = jwsObject.getPayload().toString();
            JwtPayloadDto jwtPayloadDto = JSONUtil.toBean(jwtPayloadStr, JwtPayloadDto.class);

        } catch (ParseException e) {
            e.printStackTrace();
            return Mono.just(new AuthorizationDecision(false));
        }

        // 校验访问权限
        resourceRolesMap = this.getResourceRolesMap();
        if (resourceRolesMap == null || resourceRolesMap.size() < 1) {
            return Mono.just(new AuthorizationDecision(false));
        }
        Iterator<String> iterator = resourceRolesMap.keySet().iterator();
        List<String> authorities = new ArrayList<>();
        while (iterator.hasNext()) {
            String pattern = iterator.next();
            if (pathMatcher.match(pattern, path)) {
                authorities.addAll(Convert.toList(String.class, resourceRolesMap.get(pattern)));
            }
        }
        // 角色前缀
        authorities = authorities.stream().map(i -> i = AuthConstant.AUTHORITY_PREFIX + i).collect(Collectors.toList());

        // 认证通过且角色匹配的用户可访问当前路径
        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authorities::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * 访问路径去掉指定前面 n 级路径
     * @param path 访问路径 uri.getPath()
     * @param n 去掉几级路径
     * @return
     */
    private static String StripPathPrefix(String path, int n) {
        if (StringUtils.isBlank(path)) {
            return path;
        }
        String[] pathArr = path.split("/");
        if (n >= pathArr.length) {
            return "/";
        }
        StringBuffer newPath = new StringBuffer("");
        for (int i = 0; i < pathArr.length; i++) {
            if (i <= n) continue;
            newPath.append("/").append(pathArr[i]);
        }
        path = newPath.toString();
        return path.equals("") ? "/" : path;
    }

}
