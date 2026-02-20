package com.summit.chat.service.Impl.Support.Admin;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Constants.AdminConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Enum.ChatEvent;
import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import com.summit.chat.Mapper.Cache.RedisProcessor;
import com.summit.chat.Utils.GlobalIDWorker;
import com.summit.chat.Utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AdminBusinessSupport {

    @Autowired
    private RedisProcessor processor;
    
    @Autowired
    private ClientManager clientManager;

    public   void clearUserToken(String userID) {
        processor.remove(UserConstants.CACHE_USER_PROFILE_HASH + ":" + userID);
    }
    
    public void kickUser(String userID) {
        SocketIOClient client = clientManager.getClient(userID);
        if (client != null) {
            client.sendEvent(ChatEvent.KICK_USER.getType(), "您的账号已被封禁，强制下线");
            client.disconnect();
            return;
        }
        log.info("未检测到client:{}",userID);
    }
}
