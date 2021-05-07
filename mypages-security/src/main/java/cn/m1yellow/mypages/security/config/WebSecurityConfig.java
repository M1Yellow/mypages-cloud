package cn.m1yellow.mypages.security.config;

import cn.m1yellow.mypages.security.component.JwtAccessDeniedHandler;
import cn.m1yellow.mypages.security.component.JwtAuthenticationEntryPoint;
import cn.m1yellow.mypages.security.component.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired
    private JwtAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private JwtAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity.authorizeRequests();

        // 不需要保护的资源路径允许访问
        for (String url : ignoreUrlsConfig.getUrls()) {
            registry.antMatchers(url).permitAll();
        }

        // 允许跨域请求的 OPTIONS 请求
        registry.antMatchers(HttpMethod.OPTIONS).permitAll();

        // 任何请求需要身份认证
        registry.and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                // 关闭跨站请求防护及不使用 session
                .and()
                .csrf()
                .disable()
                // JWT 不需要 HttpSession 会话
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 防止 iframe 造成跨域
                .and()
                .headers()
                .frameOptions()
                .disable()
                // 自定义认证失败处理类
                .and()
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                // 自定义认证拦截器（JWT 用户名和密码校验过滤器）
                .and()
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

    }

}
