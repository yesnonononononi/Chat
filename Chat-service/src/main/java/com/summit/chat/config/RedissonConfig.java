package com.summit.chat.config;

import com.summit.chat.Properties.RedisProperty;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
public class RedissonConfig {
    RedisProperty properties;

    public RedissonConfig(RedisProperty properties) {
        this.properties = properties;
    }

    @Bean
    public RedissonClient get(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+properties.getHost()+":"+properties.getPort());
        return Redisson.create();
    }
}
