package com.summit.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 自定义RedisTemplate配置，用于避免Redis INFO命令解析中的Unicode转义问题
 * 同时兼容幂等防护方案的JSON字符串序列化/反序列化逻辑
 */
@Configuration
public class RedisTemplateConfig {

    /**
     * 自定义RedisTemplate Bean，配置适当的序列化器
     * 泛型<Object, Object>匹配项目注入类型，避免类型转换隐患
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 字符串序列化器：键/Hash键使用，Redis官方推荐键为字符串类型，解决Unicode转义
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        // JSON序列化器：值/Hash值使用，存储纯UTF-8 JSON字符串，兼容幂等方案解析，无二进制乱码
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // 配置序列化器
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        // 初始化模板
        template.afterPropertiesSet();
        return template;
    }

    // 移除冗余的StringRedisTemplate配置：Spring自动配置的StringRedisTemplate默认就是String序列化，足够使用
}