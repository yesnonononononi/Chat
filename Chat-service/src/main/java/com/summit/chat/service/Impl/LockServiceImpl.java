


package com.summit.chat.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.service.Lock.LockService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LockServiceImpl implements LockService {

    public LockServiceImpl(RedissonClient redisson) {
        this.redisson = redisson;
    }

    private final RedissonClient redisson;

    /**
     * 获取分布式锁 <p style = "color :red">chat : lock : key : val</p>
     * @param key 锁的名字 = key +val
     * @param val val
     * @param waitTime 线程尝试获取分布式锁时的「最大等待时间」
     * @param timeunit 时间单位
     * @return 锁的对象,获取失败或者出现问题返回空
     */
    @Override
    public RLock tryLock(@NotNull String key, @NotNull String val,@NotNull long leaseTime,@NotNull long waitTime, TimeUnit timeunit) {
        if(waitTime < 0){
            log.error("【分布式锁】参数错误: {}", BaseConstants.ARGV_ERROR);
            return null;
        }
        //格式化锁key
        String lockKey = format(key, val);

        RLock lock = redisson.getLock(lockKey);
        try {
            if (lock.tryLock(leaseTime,waitTime,timeunit)) {
                log.info("获取锁成功:{}",lock);
                return lock;
            }
            log.info("获取锁失败");
            return null;

        } catch (InterruptedException e) {
            log.error("【分布式锁】锁获取出现问题: {}",e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }catch (Exception e){
            log.error("【分布式锁】锁获取发生未知错误: {}", e.getMessage(), e);
            return null;
        }

    }

    /**
     * 放锁,当前线程id+已锁是前提
     * @param lock 当前锁的对象
     * @return
     */
    @Override
    public boolean unLock(RLock lock) {
        if(ObjectUtil.isNull(lock)){
            log.error(BaseConstants.ARGV_ERROR);
            return false;
        }
        try{
        if(lock.isLocked()&&lock.isHeldByCurrentThread()) {  //uuid+threadId uuid标识节点,threadId标识线程
            lock.unlock();
            return true;
        }
        }catch (IllegalMonitorStateException e){
            log.error("【分布式锁】锁已超时释放: {}", e.getMessage(), e);
            return false;
        }catch (Exception e){
            log.error("【分布式锁】{}", BaseConstants.UNKNOWN_ERROR);
        }
        log.info("当前锁未关闭");
        return false;
    }


    private String format(String key,String val){
       return String.format("chat:lock:%s:%s", key, val);
    }

}
