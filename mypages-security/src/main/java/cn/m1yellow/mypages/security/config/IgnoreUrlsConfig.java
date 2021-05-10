package cn.m1yellow.mypages.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置白名单资源路径
 * //@ConfigurationProperties 报错
 * 有以下几种解决方法：
 * 增加注解 @ConfigurationPropertiesScan
 * 增加注解 @EnableConfigurationProperties(value = [UserProperties::class])
 * 在类上增加注解 @Component
 * 在 @Configuration 注解的类内手动创建 @Bean
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "secure.ignored")
public class IgnoreUrlsConfig {

    private List<String> urls = new ArrayList<>();

}
