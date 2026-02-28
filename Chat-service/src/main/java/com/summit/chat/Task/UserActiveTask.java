package com.summit.chat.Task;

import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Mapper.Cache.RedisProcessor;
import com.summit.chat.Mapper.admin.UserActiveMapper;
import com.summit.chat.model.entity.User;
import com.summit.chat.model.vo.UserActiveVO;
import com.summit.chat.service.Lock.LockService;
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
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisProcessor redisProcessor;
    @Autowired
    private UserActiveMapper userActiveMapper;
    @Autowired
    private LockService lockService;


    //每天0点执行
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearUserActive() {
        //0.5,获取分布式锁
        RLock lock = lockService.tryLock(UserConstants.LOCK_USER_ACTIVE_CLEAR, UserConstants.LOCK_USER_ACTIVE_CLEAR_VAL, UserConstants.LOCK_USER_ACTIVE_LEASETIME,UserConstants.LOCK_USER_ACTIVE_WAITTIME,TimeUnit.MILLISECONDS);
        if(lock == null)return;
        List<String> userIdList = new ArrayList<>();
        log.info("【工作空间】开始清除用户活跃数据");
        try {


            //1,获取所有用户id的集合
            Set<Object> range = redisTemplate.opsForZSet().range(UserConstants.CACHE_USER_ACTIVE, 0, -1);
            if (range == null || range.isEmpty()) {
                log.info("【工作空间】没有用户活跃数据");
                return;
            }

            //2,添加用户id到列表
            for (Object o : range) {
                userIdList.add(o.toString());
            }


            //2,1执行redis命令
            List<Object> res = processCache(userIdList);

            //3,获取用户信息
            int size = userIdList.size();
            if(2*size != res.size()) {
                log.error("【工作空间】用户活跃数据异常");
                return;
            }
            List<Object> userList = res.subList(0, size);
            List<Object> scoreList = res.subList(size, 2 * size);

            //4,构建vo
            List<UserActiveVO> list = buildVO(userList, scoreList, size);

            if (list.isEmpty()) {
                log.info("【工作空间】没有用户活跃数据");
                return;
            }
            Integer i = userActiveMapper.batchInsert(list);
            log.info("【工作空间】清除用户活跃数据成功,持久化数量: {}", i);
            //最后删,防丢失
            if (i != null && i > 0) redisTemplate.opsForZSet().removeRange(UserConstants.CACHE_USER_ACTIVE, 0, -1);
        } catch (Exception e) {
            log.error("【工作空间】清除用户活跃数据失败", e);
        }finally {
            lockService.unLock(lock);
        }
    }

    private ArrayList<UserActiveVO> buildVO(List<?> userList, List<?> scoreList, int size) {
        ArrayList<UserActiveVO> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                Object o = userList.get(i);
                if(o == null){
                    continue;
                }
                if(o instanceof User user) {
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
                }
            }catch (Exception e){
                log.error("【工作空间】构建用户活跃数据失败,用户id:{}", userList.get(i),e);
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
