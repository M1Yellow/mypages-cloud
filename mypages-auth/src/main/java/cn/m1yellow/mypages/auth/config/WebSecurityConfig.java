package cn.m1yellow.mypages.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * spring security 配置
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

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
        // 允许 GET 请求，否则控制台会报错
        registry.antMatchers(HttpMethod.GET).permitAll();
        registry.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll();

        // 任何请求需要身份认证
        registry.and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                //.and()
                //.formLogin() // 开启表单登录
                //.loginPage("/login.html") // 自定义登录页面
                //.loginProcessingUrl("xxx") // formLogin 中对应的 action 登录接口
                // 关闭跨站请求防护及不使用 session
                .and()
                .csrf()
                .disable()
                // JWT 不使用 HttpSession 会话
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 防止 iframe 造成跨域
                .and()
                .headers()
                .frameOptions()
                .disable() // 相当于 header 参数 "X-Frame-Options", "DENY"
                // 开启后端跨域请求支持
                .and()
                .cors()
                .and()
                //.headers().defaultsDisabled() // 关掉默认缓存配置，这种方式启动会报错
                .headers().cacheControl().disable(); // 关掉默认缓存配置

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();
        //return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new MessageDigestPasswordEncoder("MD5");
    }

}
