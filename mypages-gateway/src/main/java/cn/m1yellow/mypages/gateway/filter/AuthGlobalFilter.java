package cn.m1yellow.mypages.gateway.filter;

import cn.m1yellow.mypages.common.constant.AuthConstant;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.constant.Headers;
import cn.m1yellow.mypages.common.util.CommonUtil;
import com.nimbusds.jose.JWSObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 将登录用户的JWT转化成用户信息的全局过滤器
 * 注意，要先触发 gateway 路由配置，才会触发 GlobalFilter 过滤器
 * <br>
 * https://www.oschina.net/question/2303434_2317418
 * 我之前也是碰到过这种情况，自定义了一个GlobalFilter用来验签，看日志这个Bean已经注册进容器了，但是请求就是不进这个全局过滤器。折腾了一天，各种排除依赖，各种DEBUG，
 * 后面发现，GlobalFilter居然是基于GatewayFilter起作用的，大概逻辑是，一个请求进来后先碰GatewayFilter的规则，如果碰到了才执行下面的过滤器逻辑，如果一个GatewayFilter规则都没碰到或者
 * 项目里根本就没定义GatewayFilter的话，就会报404。如果你是这种情况的话，建议看看你所请求的路径是不是能被已有的GatewayFilter所收纳
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 非JWT或者JWT为空不作处理
        String token = exchange.getRequest().getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
        if (StringUtils.isBlank(token) || !token.startsWith(AuthConstant.JWT_TOKEN_PREFIX)) {
            return chain.filter(exchange);
        }

        // 设置用户信息到 header
        String realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
        this.parseAndSetUserInfoIntoHeader(exchange, request, realToken);

        // 设置缓存
        this.setResourceCache(request, response);

        // 往下执行其他的过滤链
        return chain.filter(exchange);
    }


    /**
     * 从 token 中解析用户信息并设置到 Header 中去，这样后续服务就不需要解析JWT令牌了，可以直接从请求的Header中获取到用户信息。
     *
     * @param exchange
     * @param request
     * @param realToken
     */
    private void parseAndSetUserInfoIntoHeader(ServerWebExchange exchange, ServerHttpRequest request, String realToken) {
        try {
            JWSObject jwsObject = JWSObject.parse(realToken);
            String jwtPayloadStr = jwsObject.getPayload().toString();
            log.info(">>>> AuthGlobalFilter token payload: {}", jwtPayloadStr);
            request = request.mutate().header(AuthConstant.USER_TOKEN_HEADER, jwtPayloadStr).build();
            exchange = exchange.mutate().request(request).build();
        } catch (Exception e) {
            log.error(">>>> AuthGlobalFilter 解析设置 header token 异常: {}", e.getMessage());
        }
    }


    /**
     * 设置资源缓存
     * @param request
     * @param response
     */
    private void setResourceCache(ServerHttpRequest request, ServerHttpResponse response) {
        // 如果是静态资源请求，设置缓存，普通接口请求，不缓存
        boolean isResourceRequest = false;
        PathMatcher pathMatcher = new AntPathMatcher();
        URI uri = request.getURI();
        String path = uri.getPath();
        // 微服务路径不参与
        path = CommonUtil.StripPathPrefix(path, 1);
        for (String sourcePath : GlobalConstant.STATIC_RESOURCES) {
            if (pathMatcher.match(sourcePath, path)) {
                isResourceRequest = true;
                break;
            }
        }

        if (isResourceRequest) { // 静态资源，设置缓存
            response.getHeaders().set(Headers.ACCESS_CONTROL_ALLOW_ORIGIN.getHeadName(), Headers.ACCESS_CONTROL_ALLOW_ORIGIN.getHeadValues());
            response.getHeaders().set(Headers.ACCESS_CONTROL_ALLOW_CREDENTIALS.getHeadName(), Headers.ACCESS_CONTROL_ALLOW_CREDENTIALS.getHeadValues());
            response.getHeaders().set(Headers.ACCESS_CONTROL_ALLOW_METHODS.getHeadName(), Headers.ACCESS_CONTROL_ALLOW_METHODS.getHeadValues());
            response.getHeaders().set(Headers.ACCESS_CONTROL_MAX_AGE.getHeadName(), Headers.ACCESS_CONTROL_MAX_AGE.getHeadValues());
            response.getHeaders().set(Headers.CACHE_CONTROL_ALLOW_CACHED.getHeadName(), Headers.CACHE_CONTROL_MAX_AGE.getHeadValues());
        } else { // 接口请求，禁用缓存
            response.getHeaders().set(Headers.CACHE_CONTROL_NOT_ALLOW_CACHED.getHeadName(), Headers.CACHE_CONTROL_NOT_ALLOW_CACHED.getHeadValues());
            response.getHeaders().set(Headers.PRAGMA.getHeadName(), Headers.PRAGMA.getHeadValues());
        }
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
