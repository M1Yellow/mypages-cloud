package cn.m1yellow.mypages.gateway.filter;

import cn.m1yellow.mypages.common.constant.AuthConstant;
import com.nimbusds.jose.JWSObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;

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

        try {
            // 从 token 中解析用户信息并设置到 Header 中去
            // 将JWT令牌中的用户信息解析出来，然后存入请求的Header中，这样后续服务就不需要解析JWT令牌了，可以直接从请求的Header中获取到用户信息。
            String realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String jwtPayloadStr = jwsObject.getPayload().toString();
            log.info("AuthGlobalFilter.filter() payload: {}", jwtPayloadStr);
            request = request.mutate().header(AuthConstant.USER_TOKEN_HEADER, jwtPayloadStr).build();
            exchange = exchange.mutate().request(request).build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
