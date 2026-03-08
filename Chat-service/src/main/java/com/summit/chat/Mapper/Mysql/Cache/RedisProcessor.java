package com.summit.chat.Mapper.Mysql.Cache;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
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
        try {
            opsStr.set(key, StrUtil.toString(value));
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("【Redis缓存】设置缓存失败, key: {}, 错误: {}", key, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("【Redis缓存】设置缓存时发生未知错误, key: {}", key, e);
            throw new RuntimeException("缓存操作失败", e);
        }
    }

    public void set(String key, Object value,Long duration,TimeUnit timeUnit) {
        try {
            opsStr.set(key, StrUtil.toString(value),duration,timeUnit);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("【Redis缓存】设置带过期时间的缓存失败, key: {}, 错误: {}", key, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("【Redis缓存】设置带过期时间的缓存时发生未知错误, key: {}", key, e);
            throw new RuntimeException("缓存操作失败", e);
        }
    }


    /**
     * 获取缓存 str
     * @param key 缓存key
     * @param type 缓存对象类型
     * @return 缓存对象
     */
    public <T> T get(String key, Class<? extends T> type) {
        try {
            String res = opsStr.get(key);
            if(StrUtil.isBlank(res)){
                return null;
            }
            return JSONUtil.parse(res).toBean(type);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("【Redis缓存】获取缓存失败, key: {}, 错误: {}", key, e.getMessage());
            return null; // 缓存获取失败时返回null，不影响业务流程
        } catch (Exception e) {
            log.error("【Redis缓存】获取缓存时发生未知错误, key: {}", key, e);
            return null;
        }
    }


    public void expire(String key, Long duration, TimeUnit timeUnit) {
        try {
            redis.expire(key, duration, timeUnit);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("【Redis缓存】设置过期时间失败, key: {}, 错误: {}", key, e.getMessage());
        } catch (Exception e) {
            log.error("【Redis缓存】设置过期时间时发生未知错误, key: {}", key, e);
        }
    }


    public boolean exist(String key) {
        try {
            return redis.hasKey(key);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("【Redis缓存】检查key是否存在失败, key: {}, 错误: {}", key, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("【Redis缓存】检查key是否存在时发生未知错误, key: {}", key, e);
            return false;
        }
    }

    /**
     * 将用户信息放入缓存
     * @param hashName 缓存全名
     * @param entry 用户实体对象
     */

    public void putAll(String hashName, Object entry) {
        try {
            Map<String, Object> map = BeanUtil.beanToMap(entry);
            map.replaceAll((key,value)->StrUtil.toStringOrEmpty(value));
            opsHash.putAll(hashName, map);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("【Redis缓存】批量设置hash失败, hashName: {}, 错误: {}", hashName, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("【Redis缓存】批量设置hash时发生未知错误, hashName: {}", hashName, e);
            throw new RuntimeException("缓存操作失败", e);
        }
    }


    public void put(String hashName, String key, Object value) {
        try {
            opsHash.put(hashName, key, value);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("【Redis缓存】设置hash字段失败, hashName: {}, key: {}, 错误: {}", hashName, key, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("【Redis缓存】设置hash字段时发生未知错误, hashName: {}, key: {}", hashName, key, e);
            throw new RuntimeException("缓存操作失败", e);
        }
    }


    public String get(String hashName, String key) {
        try {
            Object o = opsHash.get(hashName, key);
            if(o==null){
                return null;
            }
            return o.toString();
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("【Redis缓存】获取hash字段失败, hashName: {}, key: {}, 错误: {}", hashName, key, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("【Redis缓存】获取hash字段时发生未知错误, hashName: {}, key: {}", hashName, key, e);
            return null;
        }
    }

    public Map getAllForHash(String key){
        try {
            //获取哈希的所有键值对
            Map<Object, Object> entries = opsHash.entries(key);
            return entries;
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("【Redis缓存】获取hash所有字段失败, key: {}, 错误: {}", key, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("【Redis缓存】获取hash所有字段时发生未知错误, key: {}", key, e);
            return null;
        }
    }

    

    public Boolean deleteHash(String key) {
        try {
            return redis.delete(key);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("【Redis缓存】删除hash失败, key: {}, 错误: {}", key, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("【Redis缓存】删除hash时发生未知错误, key: {}", key, e);
            return false;
        }
    }

    public List<Object> executePipelined(SessionCallback<?> session) {
        try {
            return redis.executePipelined(session);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("【Redis缓存】执行管道操作失败, 错误: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("【Redis缓存】执行管道操作时发生未知错误", e);
            throw new RuntimeException("缓存管道操作失败", e);
        }
    }

    public void remove(String key){
        try {
            redis.delete(key);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("【Redis缓存】删除key失败, key: {}, 错误: {}", key, e.getMessage());
        } catch (Exception e) {
            log.error("【Redis缓存】删除key时发生未知错误, key: {}", key, e);
        }
    }

}
