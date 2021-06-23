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
import com.sun.jndi.toolkit.url.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.nio.charset.StandardCharsets;
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

        ServerWebExchange exchange = authorizationContext.getExchange();
        ServerHttpRequest request = exchange.getRequest();
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
        //String userIdStr = authorizationContext.getExchange().getAttribute("userId"); // 没值
        // TODO 获取 GET 请求参数，后续再提取成公共方法
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        if (queryParams != null && queryParams.size() > 0) {
            List<String> resultList = queryParams.get("userId"); // [1]，结果是List<String>，有多个值
            if (resultList != null && resultList.size() > 0) {
                String userIdStr = resultList.get(0); // [1]，结果是List<String>，有多个值
                if (StringUtils.isNotBlank(userIdStr)) {
                    try {
                        userId = Long.parseLong(userIdStr);
                    } catch (NumberFormatException e) {
                        log.error(">>>> token 解析 queryParams 中的 userId 异常: {}", e.getMessage());
                    }
                }
            }
        }

        // TODO 获取 POST 请求参数，后续再提取成公共方法
        if (userId == null) {
            String method = request.getMethodValue();
            String contentType = request.getHeaders().getFirst("Content-Type");
            if ("POST".equals(method) && StringUtils.isNotBlank(contentType)) {
                String postBodyStr = this.resolveBodyFromRequest(request);
                log.info(">>>> post body={}", postBodyStr);
                // TODO POST 参数处理
                if (contentType.contains("multipart/form-data")) { // multipart/form-data 文件和表单形式

                } else if (contentType.contains("x-www-form-urlencoded")) { // x-www-form-urlencoded 普通键值对形式
                    // userName=admin&userId=1
                    Map<String, Object> postParams = CommonUtil.getUrlParams(postBodyStr);
                    try {
                        userId = Long.parseLong(ObjectUtil.getString(postParams.get("userId")));
                    } catch (Exception e) {
                        log.error(">>>> token 解析 post body 中的 userId 异常: {}", e.getMessage());
                    }
                } else if (contentType.contains("json")) { // json 形式

                }
            }
        }

        if (userId != null) {
            if (!userId.equals(jwtPayloadDto.getUserId())) {
                // token 身份不对应
                log.error(">>>> token 身份不对应，request userId={}, jwt userId={}", userId, jwtPayloadDto.getUserId());
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


    /**
     * 从Flux<DataBuffer>中获取字符串的方法
     *
     * @return 请求体
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();

        /*
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        //获取request body
        return bodyRef.get();
        */

        StringBuilder sb = new StringBuilder();
        body.subscribe(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        });
        return sb.toString();
    }


}
