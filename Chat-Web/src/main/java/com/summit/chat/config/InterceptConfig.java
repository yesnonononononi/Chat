package com.summit.chat.config;


import com.summit.chat.Intercept.Interceptor;
import com.summit.chat.Json.JacksonObjectMapper;
import com.summit.chat.Mapper.Cache.RedisProcessor;
import com.summit.chat.Mapper.UserMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptConfig implements WebMvcConfigurer {

    @Autowired
    RedisProcessor processor;
    @Autowired
    private UserMapper userMapper;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new Interceptor(processor,userMapper)).excludePathPatterns(
                "/user/password-login",
                "/user/send-sms-code",
                "/user/sms-login",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**",
                "/user/forget",
                "/sign"
        );

    }



    @Override
    public void configureMessageConverters(HttpMessageConverters.@NonNull ServerBuilder builder) {
        // 调用父类方法（保留 Spring 默认的基础转换器，避免丢失非 JSON/CBOR 处理能力）
        WebMvcConfigurer.super.configureMessageConverters(builder);

        // 2. 配置【JSON 转换器】（替换默认的 JSON 处理，适配前端登录页的响应格式）
        // 使用自定义的 customJacksonObjectMapper，其中包含了Long转String的序列化器
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(new JacksonObjectMapper());

        // 4. 将自定义转换器添加到 ServerBuilder，并控制优先级（核心步骤）
        // - prepend：将转换器添加到「最前面」，确保优先使用自定义配置（覆盖默认转换器）
        // - 顺序：先 JSON 再 CBOR（按业务需求调整，JSON 通常是主要格式）
        builder.withJsonConverter(jsonConverter);
        // （可选）若需删除默认的同类型转换器（避免冲突），可通过 filter 过滤
        // 例如：删除默认的 JacksonJsonHttpMessageConverter，只保留自定义的
        // builder.converters().removeIf(converter -> converter instanceof JacksonJsonHttpMessageConverter);
    }
}

