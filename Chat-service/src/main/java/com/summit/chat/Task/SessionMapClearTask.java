package com.summit.chat.Task;

import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionMapClearTask {
    @Autowired
    private ClientManager clientManager;


    /**
     * 定时任务,清理本地僵尸会话缓存
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void clearSessionMap() {
        log.info("【定时任务-本地会话清理】开始清理本地会话缓存");
        clientManager.clearInvalidMap();
        log.info("【定时任务-本地会话清理】成功清理本地会话缓存");
    }


}
