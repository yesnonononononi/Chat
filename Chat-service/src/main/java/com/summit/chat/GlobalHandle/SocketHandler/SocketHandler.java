package com.summit.chat.GlobalHandle.SocketHandler;


import com.corundumstudio.socketio.SocketIOServer;
import com.summit.chat.Enum.ChatEvent;
import com.summit.chat.GlobalHandle.SocketHandler.GroupSocketHandler.GroupDataListener;
import com.summit.chat.GlobalHandle.SocketHandler.PrivateSocketHandler.DataListener;
import com.summit.chat.model.vo.GroupMessageVO;
import com.summit.chat.model.vo.PrivateMessageVO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class  SocketHandler  {
    @Autowired
    SocketIOServer socketIOServer;
    @Autowired
    ConnectionListener connectionListener;
    @Autowired
    DisConnectionListener disConnectionListener;
    @Autowired
    DataListener dataListener;
    @Autowired
    GroupDataListener groupDataListener;


    @PostConstruct
    public void init(){
        try {
            // 添加事件监听器
            socketIOServer.addConnectListener(connectionListener);
            socketIOServer.addDisconnectListener(disConnectionListener);
            socketIOServer.addEventListener(ChatEvent.GROUP_MSG_SEND.getType(), GroupMessageVO.class,groupDataListener);
            socketIOServer.addEventListener(ChatEvent.PRIVATE_MSG_SEND.getType(), PrivateMessageVO.class,dataListener);

            socketIOServer.start();
        } catch (Exception e) {
            log.error("服务开启失败", e);
        }
    }






    @PreDestroy
    public void destroy(){
        if (socketIOServer != null) {
            socketIOServer.stop();
            log.info("SocketIOServer stopped");
        }
    }
}
