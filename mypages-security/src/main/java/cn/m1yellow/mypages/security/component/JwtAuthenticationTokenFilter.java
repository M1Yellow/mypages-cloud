package cn.m1yellow.mypages.security.component;

import cn.m1yellow.mypages.security.bo.SecurityUser;
import cn.m1yellow.mypages.security.config.IgnoreUrlsConfig;
import cn.m1yellow.mypages.security.config.JwtSecurityProperties;
import cn.m1yellow.mypages.security.util.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 登录验证过滤器
 */
@Order(1) // 数值越小，越先执行
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtSecurityProperties jwtSecurityProperties;
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        /**
         * HttpMethod: GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
         * OPTIONS请求方法的主要用途有两个：
         * 1、获取服务器支持的HTTP请求方法，所以可以用来探测。
         * 2、用来验证接口功能，也就是提前验证一下。
         */
        // OPTIONS 请求直接放行
        if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
            chain.doFilter(request, response);
            return;
        }

        // 白名单请求直接放行
        PathMatcher pathMatcher = new AntPathMatcher();
        String uri = request.getRequestURI();
        for (String path : ignoreUrlsConfig.getUrls()) {
            if (pathMatcher.match(path, uri)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 获取 auth 标识：Authorization
        String authHeader = request.getHeader(jwtSecurityProperties.getTokenHeader());

        if (StringUtils.isNotBlank(authHeader) && authHeader.startsWith(jwtSecurityProperties.getTokenStart())) {

            // 获取 "Bearer " 之后的 token
            String authToken = authHeader.substring(jwtSecurityProperties.getTokenStart().length());
            // 从 token 中获取用户名
            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            logger.info(">>>> check token username: {}", username);

            if (StringUtils.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                // spring security 用户详情对象封装
                SecurityUser userDetails = (SecurityUser) userDetailsService.loadUserByUsername(username);
                // 验证用户 token
                if (jwtTokenUtil.validateUserToken(authToken, userDetails)) {
                    // spring security 用户名和密码校验规则
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    logger.info(">>>> authenticated user: {}", username);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // 放行，继续往下执行
        chain.doFilter(request, response);
    }
}
