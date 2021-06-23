package cn.m1yellow.mypages.gateway.authorization;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import cn.m1yellow.mypages.common.constant.AuthConstant;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.domian.JwtPayloadDto;
import cn.m1yellow.mypages.common.util.CommonUtil;
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
        path = CommonUtil.StripPathPrefix(path, 1);
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

        // token 解析
        String realToken = null;
        JwtPayloadDto jwtPayloadDto = null;
        try {
            String token = request.getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
            if (StringUtils.isBlank(token)) {
                return Mono.just(new AuthorizationDecision(false));
            }
            realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String jwtPayloadStr = jwsObject.getPayload().toString();
            jwtPayloadDto = JSONUtil.toBean(jwtPayloadStr, JwtPayloadDto.class);

        } catch (ParseException e) {
            log.error(">>>> token 解析异常: {}", e.getMessage());
            return Mono.just(new AuthorizationDecision(false));
        }

        // 校验 token 身份
        // TODO 注意，个别接口可能没有 userId 参数
        Long userId = null;
        String userIdStr = authorizationContext.getExchange().getAttribute("userId");
        if (StringUtils.isNotBlank(userIdStr)) {
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                log.error(">>>> token 解析 userId 异常: {}", e.getMessage());
            }
        }
        if (userId != null) {
            if (!userId.equals(jwtPayloadDto.getUserId())) {
                // token 身份不对应
                return Mono.just(new AuthorizationDecision(false));
            }
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

}
