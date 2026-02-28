package com.summit.chat.service.Impl.Support.User;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserEsSupport {
    @Autowired
    private ElasticsearchClient elasticsearchClient;




}
