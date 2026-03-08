package com.summit.chat.config;


import com.summit.chat.Intercept.Interceptor;
import com.summit.chat.Json.JacksonObjectMapper;
import com.summit.chat.Mapper.Mysql.Cache.RedisProcessor;
import com.summit.chat.Mapper.Mysql.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

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
                "/sign",
                "/chat-io/**"
        );

    }



    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Remove default Jackson converter to ensure our custom one is used
        converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
        
        // Add custom converter with JacksonObjectMapper (handles Long -> String)
        converters.add(0, new MappingJackson2HttpMessageConverter(new JacksonObjectMapper()));
    }
}

