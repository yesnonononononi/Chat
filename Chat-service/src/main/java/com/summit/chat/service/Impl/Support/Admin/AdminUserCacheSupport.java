package com.summit.chat.service.Impl.Support.Admin;

import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Mapper.Cache.RedisProcessor;
import com.summit.chat.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 管理员用户缓存支持类
 * 专门用于处理用户相关的缓存操作，使用Hash结构
 */
@Slf4j
@Component
public class AdminUserCacheSupport {

    @Autowired
    private RedisProcessor redisProcessor;

    /**
     * 清除用户缓存 (Hash结构)
     * 对应 key: user:profile:{userId}
     *
     * @param userId 用户ID
     */
    public void evictUserProfileCache(String userId) {
        if (userId == null) {
            return;
        }
       
        
        // "user:profile:" + userId
        String key = UserConstants.CACHE_USER_PROFILE_HASH + ":" + userId;
        
        
        redisProcessor.remove(key);
        log.info("【管理员缓存】清除用户缓存成功, userId: {}", userId);
        
     
    }
}
