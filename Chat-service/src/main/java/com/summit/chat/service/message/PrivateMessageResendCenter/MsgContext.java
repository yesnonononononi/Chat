package com.summit.chat.service.message.PrivateMessageResendCenter;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.model.entity.mysql.Msg;
import lombok.Data;

@Data
public class MsgContext<T extends Msg> {
    private T msg;
    private SocketIOClient emitterClient;
    private SocketIOClient receiveClient;
    private boolean isOnline;  //转发对象在线状态
    public MsgContext(SocketIOClient emitterClient) {
        this.emitterClient = emitterClient;
    }
    private AckRequest ackRequest;

}

