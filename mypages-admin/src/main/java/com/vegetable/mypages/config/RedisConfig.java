package com.vegetable.mypages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 将 template 泛型设置为 <String, Object>
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 连接工厂，默认
        template.setConnectionFactory(redisConnectionFactory);

        // key、hash 的 key 采用 String 序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //template.setHashKeySerializer(RedisSerializer.string());
        // value、hash 的 value 采用 Jackson 序列化方式
        template.setDefaultSerializer(RedisSerializer.json());
        //template.setValueSerializer(RedisSerializer.json());
        //template.setHashValueSerializer(RedisSerializer.json());
        template.afterPropertiesSet();
        return template;
    }
}