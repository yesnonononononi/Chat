package com.summit.chat.Mapper.Mysql.Cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
@Slf4j
@Component
public class CacheSupport implements Cache{

    private final ObjectMapper objectMapper;
    RedisProcessor processor;
    Validator validator;
    public CacheSupport(RedisProcessor processor, Validator validator, ObjectMapper objectMapper) {
        this.processor = processor;
        this.validator = validator;
        this.objectMapper = objectMapper;
    }

    public <T>T get(String  tableId,Class<T>tClass){
        return processor.get(tableId, tClass);
    }

    private String buildKey(String cacheName,String key){
        return cacheName + ":" + key;
    }




    public void expire(String cacheName, String key, Long duration, TimeUnit timeUnit){
        processor.expire(buildKey(cacheName,key),duration,timeUnit);
    }

    public void putAll(String hashName,Object value) {
        processor.putAll(hashName,value);
    }

    public void put(String name,String key,String value){
        processor.put(name,key,value);
    }

    public <T>void set(String key,T t,Long duration,TimeUnit timeUnit) {
        processor.set(key,t,duration,timeUnit);
    }

    public List<Object> executePipelined(SessionCallback<?> session) {
        return processor.executePipelined(session);
    }
}

