package com.summit.chat.service.Impl.Support.MsgSupport;

import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Constants.MsgConstants;
import com.summit.chat.Dto.MessageWithdrawnDTO;
import com.summit.chat.Enum.ChatEvent;
import com.summit.chat.Enum.MsgEnum;
import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import com.summit.chat.model.vo.MsgCallbackForPrivateVO;
import com.summit.chat.model.vo.PrivateMessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrivateMsgSupport {
    @Autowired
    private ClientManager clientManager;
    public void withdrawn(PrivateMessageVO vo) {
        String emitterId = vo.getEmitterId();
        String receiveId = vo.getReceiveId();
        String msgId = vo.getMsgId();
        MessageWithdrawnDTO aPrivate = MessageWithdrawnDTO.builder()
                .emitterId(emitterId)
                .receiverId(receiveId)
                .type("private")
                .msgId(msgId)
                .build();
        SocketIOClient client = clientManager.getClient(receiveId);
        if (client != null) {
            client.sendEvent(ChatEvent.CHAT_WITHDRAWN.getType(), aPrivate);
        }
    }

    public void read(String emitterId) {
        SocketIOClient client = clientManager.getClient(emitterId);
        if (client != null) {
            client.sendEvent(ChatEvent.CHAT_DELIVERED.getType(), MsgCallbackForPrivateVO.builder().msgId(MsgConstants.DEFAULT_READ_ALL).msgCode(MsgEnum.READ.getStatus()).description("").symbol("").build());
        }
    }
}
