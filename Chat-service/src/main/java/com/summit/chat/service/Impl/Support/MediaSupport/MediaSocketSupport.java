package com.summit.chat.service.Impl.Support.MediaSupport;

import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Dto.MediaApplyDTO;
import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MediaSocketSupport {
    @Autowired
    ClientManager clientManager;

    private final String MEDIA_APPLY_EVENT_SEND = "media_apply_send";
    private final String MEDIA_APPLY_EVENT_ACCEPT = "media_apply_accept";
    private final String MEDIA_APPLY_EVENT_REJECT = "media_apply_reject";
    private final String MEDIA_APPLY_EVENT_CANCEL = "media_apply_cancel";

    public SocketIOClient getClient(String userId){
        return clientManager.getClient(userId);
    }

    public void send(SocketIOClient client, MediaApplyDTO dto){
        client.sendEvent(MEDIA_APPLY_EVENT_SEND, dto);
        log.info("【视频聊天】发送申请成功,接收者id:{},申请者id:{}", dto.getReceiverId(), dto.getUserId());
    }

    public void call(SocketIOClient client,String receiveId ){
        client.sendEvent(MEDIA_APPLY_EVENT_ACCEPT, receiveId);
    }

    public void reject(SocketIOClient client, String emitterId) {
        client.sendEvent(MEDIA_APPLY_EVENT_REJECT, emitterId);
    }

    public void cancel(SocketIOClient client, String emitterId) {
        client.sendEvent(MEDIA_APPLY_EVENT_CANCEL, emitterId);
    }
}
