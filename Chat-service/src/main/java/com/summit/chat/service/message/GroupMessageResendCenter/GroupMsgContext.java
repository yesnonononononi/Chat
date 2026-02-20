package com.summit.chat.service.message.GroupMessageResendCenter;

import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import com.summit.chat.model.vo.GroupMessageVO;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;

@Data
@Builder
public class GroupMsgContext{
    private GroupMessageVO msg;
    private HashSet<SocketIOClient>clients;
    private ClientManager clientManager;
}
