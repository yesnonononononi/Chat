package com.summit.chat.Mapper.Cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
@Slf4j
@Component
public class CacheSupport implements Cache{

    RedisProcessor processor;
    Validator validator;
    public CacheSupport(RedisProcessor processor,Validator validator) {
        this.processor = processor;
        this.validator = validator;
    }

    public <T>T get(String  tableId,Class<T>tClass){
        return processor.get(tableId, tClass);
    }

    private String buildKey(String cacheName,String key){
        return cacheName + ":" + key;
    }

    /**
     * 查询操作+缓存
     * @param cacheName 缓存业务前缀如:user:profile
     * @param tableId 缓存键:这里为id: user:profile:<id>
     * @param function 数据库查询操作函数
     * @return 返回查询到的结果,如果为null,代表没有查询到结果
     * @param <T> 方法的参数类型
     * @param <R> 方法的返回值
     */
    public <T,R> R get(String cacheName,T tableId , Function<T,R> function){
        String id = String.valueOf(tableId);
        //从缓存中获取
        Map allForHash = processor.getAllForHash(buildKey(cacheName, id));

        //检查是否获取到
        if (validator.baseValidate(allForHash)) {
            //获取到了,就返回
            return (R) allForHash;
        }
        //否则查数据库

        return function.apply(tableId);
    }


    public <T,R> void edit(String cacheName,T tableId,Function<T,R>function){
        //直接改数据库
        function.apply(tableId);
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

