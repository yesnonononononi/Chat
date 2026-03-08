package com.summit.chat.service.Impl.Support.Admin;

import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Enum.ChatEvent;
import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import com.summit.chat.Mapper.Mysql.Cache.RedisProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
