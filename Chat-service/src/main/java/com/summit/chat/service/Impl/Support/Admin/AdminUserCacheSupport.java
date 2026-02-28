package com.summit.chat.service.Impl.Support.Admin;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Mapper.Cache.RedisProcessor;
import com.summit.chat.Mapper.UserMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.User;
import com.summit.chat.model.vo.UserActiveVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理员用户缓存支持类
 * 专门用于处理用户相关的缓存操作，使用Hash结构
 */
@Slf4j
@Component
public class AdminUserCacheSupport {

    @Autowired
    private RedisProcessor redisProcessor;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ObjectMapper objectMapper;

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

    public List<UserActiveVO> getUserActive() {
        // 1. 校验TOP长度合法性，避免无效查询
        int topLength = UserConstants.USER_ACTIVE_TOP_LENGTH;

        // 2. 替换popMax为rangeWithScores（仅查询，不移除数据），获取TOP N的用户（按分值降序）
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet()
                .rangeWithScores(UserConstants.CACHE_USER_ACTIVE, 0, topLength - 1); // 0到topLength-1是前N个元素

        // 3. 空值判断（简化写法）
        if (CollectionUtils.isEmpty(typedTuples)) {
            log.info("【用户活跃度】暂无用户活跃数据");
            return Collections.emptyList();
        }

        // 4. 流式处理：过滤异常数据，不中断整体流程
        return typedTuples.stream()
                .filter(Objects::nonNull) // 过滤空的Tuple
                .map(typedTuple -> {
                    try {
                        Object value = typedTuple.getValue();
                        Double score = typedTuple.getScore();

                        // 5. 空值校验：过滤无效数据，不返回null
                        if (value == null || score == null) {
                            log.warn("【用户活跃度】Tuple数据异常，value: {}, score: {}", value, score);
                            return null;
                        }
                        String userId = value.toString(); // 提前转换userId，便于日志打印

                        // 6. 获取用户信息（补充userId日志，精准定位）
                        User user = getUserById(userId);
                        if (user == null) {
                            log.warn("【用户活跃度】用户信息不存在，userId: {}", userId);
                            return null;
                        }

                        // 7. 构建VO并返回
                        return UserActiveVO.builder()
                                .userID(userId)
                                .active(score.intValue())
                                .nickName(user.getNickName())
                                .icon(user.getIcon())
                                .build();
                    } catch (Exception e) {
                        // 8. 捕获异常：打印详细日志，不抛出异常（避免中断流）
                        String userId = typedTuple.getValue() != null
                                ? typedTuple.getValue().toString()
                                : "未知";
                        log.warn("【用户活跃度】获取用户数据失败，userId: {}", userId, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull) // 过滤掉所有null的VO，保证返回列表无空元素
                .collect(Collectors.toList()); // 替换toList()为collect（兼容低版本JDK，且更可控）
    }


    public User getUserById(String userId) throws IOException {
        try {
            // 从Redis获取缓存数据
            Result cachedResult = redisProcessor.get(UserConstants.CACHE_USER_PROFILE_HASH + ':' + userId, Result.class);
            if(cachedResult != null){
                Object userVO = cachedResult.getData();
                if (userVO != null) {
                    return BeanUtil.copyProperties(userVO, User.class,"pw");
                }
            }

            // 缓存未命中或解析失败，从数据库查询
            User userById = userMapper.getUserById(Long.valueOf(userId));

            return userById;
        } catch (Exception e) {
            log.warn("【用户缓存】从缓存获取用户信息失败, userId: {}", userId, e);
            return null;
        }

    }
}
