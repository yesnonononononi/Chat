package com.summit.chat.config;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest5_client.Rest5ClientTransport;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class ElasticConfiguration {
    @Value("${spring.elasticsearch.uris}")
    private String uris;
    @Autowired
    private ObjectMapper objectMapper;


    @Bean
    public ElasticsearchClient elasticsearchClient() {
        Rest5Client build = Rest5Client.builder(URI.create(uris))
                .build();
        Rest5ClientTransport rest5ClientTransport = new Rest5ClientTransport(build, new JacksonJsonpMapper(objectMapper));
        return new ElasticsearchClient(rest5ClientTransport);
    }
}
