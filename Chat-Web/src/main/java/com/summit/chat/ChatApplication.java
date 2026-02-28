package com.summit.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableAsync
@EnableCaching
@EnableConfigurationProperties
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.summit.chat.Mapper")
@EnableWebSocket
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
})
public class ChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

    @Bean
    public org.springframework.messaging.converter.JacksonJsonMessageConverter jsonbMessageConverter(){
        return new JacksonJsonMessageConverter();
    }


}