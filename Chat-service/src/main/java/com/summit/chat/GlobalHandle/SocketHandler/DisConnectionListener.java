package com.summit.chat.GlobalHandle.SocketHandler;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DisConnectionListener implements DisconnectListener {
    @Resource
    ClientManager manager;

    @Override
    public void onDisconnect(SocketIOClient socketIOClient) {

        log.info("【Socket断开】断开客户端:{}", socketIOClient.getSessionId());
        //移除客户
        manager.removeClient(socketIOClient);

    }
}
