package com.summit.chat.service.Lock;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

public interface LockService {
    /**
     * 获取分布式锁 <p style = "color :red">chat : lock : key : val</p>
     * @param key 锁的名字 = key +val
     * @param val val
     * @param waitTime 线程尝试获取分布式锁时的「最大等待时间」
     * @param timeUnit 时间单位
     * @return 锁的对象,获取失败或者出现问题返回空
     */
    RLock tryLock(String key, String val,long leaseTime, long waitTime, TimeUnit timeUnit);
    boolean unLock(RLock lock);
}
