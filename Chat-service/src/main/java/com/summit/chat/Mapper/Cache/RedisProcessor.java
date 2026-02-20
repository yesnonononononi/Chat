package com.summit.chat.Mapper.Cache;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.SessionCallback;
import java.util.List;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;
@Slf4j
@Service
public class RedisProcessor implements Cache{

    StringRedisTemplate redis;
    HashOperations<String, Object, Object> opsHash;
    ValueOperations<String, String> opsStr;

    public RedisProcessor(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @PostConstruct
    void init() {
        opsHash = redis.opsForHash();
        opsStr = redis.opsForValue();
    }



    public void set(String key, Object value) {
        opsStr.set(key, StrUtil.toString(value));
    }

    public void set(String key, Object value,Long duration,TimeUnit timeUnit) {
        opsStr.set(key, StrUtil.toString(value),duration,timeUnit);
    }


    public <T> T get(String key, Class<? extends T> type) {
        String res = opsStr.get(key);
        if(StrUtil.isBlank(res)){
            return null;
        }
        return JSONUtil.parse(res).toBean(type);
    }


    public void expire(String key, Long duration, TimeUnit timeUnit) {
        redis.expire(key, duration, timeUnit);
    }


    public boolean exist(String key) {

        return redis.hasKey(key);
    }

    /**
     * 将用户信息放入缓存
     * @param hashName 缓存全名
     * @param entry 用户实体对象
     */

    public void putAll(String hashName, Object entry) {
        Map<String, Object> map = BeanUtil.beanToMap(entry);
        map.replaceAll((key,value)->StrUtil.toStringOrEmpty(value));
        opsHash.putAll(hashName, map);
    }


    public void put(String hashName, String key, Object value) {
        opsHash.put(hashName, key, value);
    }


    public String get(String hashName, String key) {
        Object o = opsHash.get(hashName, key);
        if(o==null){
            return null;
        }
        return o.toString();
    }

    public Map getAllForHash(String key){
        //获取哈希的所有键值对
        Map<Object, Object> entries = opsHash.entries(key);
        return entries;
    }

    

    public Boolean deleteHash(String key) {
        return redis.delete(key);  // 修复：使用redis.delete()删除整个哈希表，而不是opsHash.delete()
    }

    public List<Object> executePipelined(SessionCallback<?> session) {
        return redis.executePipelined(session);
    }

    public void remove(String key){
         redis.delete(key);
    }

}
