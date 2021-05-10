package cn.m1yellow.mypages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

/**
 * WebCorsConfiguration 跨域配置
 * 注意：如果使用了 Spring Security，跨域配置会失效，因为请求被 Spring Security 拦截了。
 * 在 Spring Security web 配置中，开始跨域支持即可
 */
@Configuration
public class WebCorsConfiguration implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter() {
        // 不加 final 无效？？
        //final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        //final CorsConfiguration corsConfiguration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        // 暴露 header 中的其他属性给客户端应用程序
        config.setExposedHeaders(Arrays.asList(
                "Authorization", "X-Total-Count", "Link",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));
        source.registerCorsConfiguration("/**", config); // /** 表示本应用的所有方法都会去处理跨域请求
        return new CorsFilter(source);
    }

}
