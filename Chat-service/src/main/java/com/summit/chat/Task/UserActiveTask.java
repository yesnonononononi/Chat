package com.summit.chat.Task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Mapper.Mysql.Cache.RedisProcessor;
import com.summit.chat.Mapper.Mysql.admin.UserActiveMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.model.vo.UserActiveVO;
import com.summit.chat.model.vo.UserVO;
import com.summit.chat.service.Lock.LockService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class UserActiveTask {
    @Resource
    private RedisTemplate<String, String> zSetRedisTemplate;
    @Autowired
    private RedisProcessor redisProcessor;
    @Autowired
    private UserActiveMapper userActiveMapper;
    @Autowired
    private LockService lockService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    //每天0点执行
    @Scheduled(cron = "0 55 23 * * ?")
    public void clearUserActive() {
        //0.5,获取分布式锁
        RLock lock = lockService.tryLock(UserConstants.LOCK_USER_ACTIVE_CLEAR, UserConstants.LOCK_USER_ACTIVE_CLEAR_VAL, UserConstants.LOCK_USER_ACTIVE_LEASETIME, UserConstants.LOCK_USER_ACTIVE_WAITTIME, TimeUnit.SECONDS);
        if (lock == null) return;
        List<String> userIdList = new ArrayList<>();
        log.info("【定时任务-用户活跃度持久化】开始清除用户活跃数据");
        try {


            //1,获取所有用户id的集合
            Set<String> range = zSetRedisTemplate.opsForZSet().range(UserConstants.CACHE_USER_ACTIVE, 0, -1);
            log.info("range:{}", range);
            if (range == null || range.isEmpty()) {

                log.info("【定时任务-用户活跃度持久化】没有用户活跃数据");
                return;
            }

            //2,添加用户id到列表
            userIdList.addAll(range);


            //2,1执行redis命令
            List<Object> res = processCache(userIdList);

            //3,获取用户信息
            int size = userIdList.size();
            if (2 * size != res.size()) {
                log.error("【定时任务-用户活跃度持久化】用户活跃数据异常");
                return;
            }
            List<Object> userList = res.subList(0, size);
            List<Object> scoreList = res.subList(size, 2 * size);

            //4,构建vo
            List<UserActiveVO> list = buildVO(userList, scoreList, size);

            if (list.isEmpty()) {
                log.info("【定时任务-用户活跃度持久化】没有用户活跃数据");
                return;
            }
            Integer i = userActiveMapper.batchInsert(list);
            log.info("【定时任务-用户活跃度持久化】清除用户活跃数据成功,持久化数量: {}", i);
            //最后删,防丢失
            if (i != null && i > 0) zSetRedisTemplate.opsForZSet().removeRange(UserConstants.CACHE_USER_ACTIVE, 0, -1);
        } catch (Exception e) {
            log.error("【定时任务-用户活跃度持久化】清除用户活跃数据失败", e);
        } finally {
            lockService.unLock(lock);
        }
    }

    private ArrayList<UserActiveVO> buildVO(List<?> userList, List<?> scoreList, int size) {
        ArrayList<UserActiveVO> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                Object o = userList.get(i);
                if (o == null) {
                    continue;
                }
                Result<UserVO> result = OBJECT_MAPPER.readValue(o.toString(), OBJECT_MAPPER.getTypeFactory().constructParametricType(Result.class, UserVO.class));

                try {
                    UserVO user = result.getData();
                    Double score = (Double) scoreList.get(i);
                    if (score == null) {
                        score = 0.0;
                    }
                    UserActiveVO vo = UserActiveVO.builder()
                            .active(score.intValue())
                            .userID(user.getId())
                            .icon(user.getIcon())
                            .nickName(user.getNickName())
                            .build();
                    list.add(vo);
                } catch (Exception e) {
                    log.error("【定时任务-用户活跃度持久化】用户实体类型转换失败,userID:{}", userList.get(i), e);
                }
            } catch (Exception e) {
                log.error("【定时任务-用户活跃度持久化】构建用户活跃数据失败,用户id:{}", userList.get(i), e);
            }
        }
        return list;
    }

    private List<Object> processCache(List<String> userIdList) {
        List<Object> res = redisProcessor.executePipelined(new SessionCallback<Void>() {


            @Override
            public <K, V> Void execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>) operations;
                for (String userId : userIdList) {
                    redisTemplate.opsForValue().get(UserConstants.CACHE_USER_PROFILE_HASH + ':' + userId);
                }
                for (String userId : userIdList) {
                    redisTemplate.opsForZSet().score(UserConstants.CACHE_USER_ACTIVE, userId);
                }
                return null;

            }
        });
        return res;
    }

}
