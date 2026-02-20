package com.summit.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 启用Spring Security配置
public class SecurityConfig {

    // 配置请求授权规则、关闭默认拦截等
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 配置请求权限
                .authorizeHttpRequests(auth -> auth
                        // 放行WebSocket连接端点，必须在anyRequest之前
                        .requestMatchers("/socket.io/**").permitAll()
                        // 放行其他所有请求
                        .anyRequest().permitAll()
                )
                // 关闭CSRF（前后端分离场景必须关，否则POST请求会被拦截）
                .csrf(csrf -> csrf.disable())
                // 关闭默认的表单登录（我们用Token认证，不需要表单登录页）
                .formLogin(form -> form.disable())
                // 关闭HTTP Basic认证（可选，接口服务一般用不到）
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}