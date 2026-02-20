package com.summit.chat.service.User.register;

import com.summit.chat.Constants.LockConstants;
import com.summit.chat.service.Lock.LockService;
import org.redisson.api.RLock;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 注册流程中的分布式锁管理
 * <p>
 * 仅负责获取和释放注册相关的锁，避免在 RegisterHandle 中同时处理业务和锁细节。
 */
@Component
public class RegisterLockManager {

    private final LockService lockService;

    public RegisterLockManager(LockService lockService) {
        this.lockService = lockService;
    }

    public RLock getLock(String val) {
        // 3L,10L
        return lockService.tryLock("register", val,
                LockConstants.LOCK_REGISTER_WAITTIME,
                LockConstants.LOCK_REGISTER_LEASETIME,
                TimeUnit.SECONDS);
    }

    public boolean unLock(RLock lock) {
        return lockService.unLock(lock);
    }
}


