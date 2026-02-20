package com.summit.chat.GlobalHandle.SocketHandler.PrivateSocketHandler;


import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Dto.MsgAckDTO;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.service.message.PrivateMessageResendCenter.MsgForPrivateChainProcessor;
import com.summit.chat.model.vo.PrivateMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataListener implements com.corundumstudio.socketio.listener.DataListener<PrivateMessageVO> {
   @Autowired
    MsgForPrivateChainProcessor msgForPrivateChainProcessor;

    @Override
    public void onData(SocketIOClient socketIOClient, PrivateMessageVO o, AckRequest ackRequest) {
        log.info("【私聊消息转发】收到来自客户端:{},消息:{},发送给用户:{}", socketIOClient.getSessionId(), o.getMsg(), o.getReceiveId());
        try {
          msgForPrivateChainProcessor.start(socketIOClient,o,ackRequest);

        } catch (BusinessException e) {

            log.error("【私聊消息转发】用户:{}转发消息遇到问题", o.getEmitterId(), e);

            ackRequest.sendAckData(MsgAckDTO.error(e.getMessage()));

        } catch (Exception e) {
            log.error("【私聊消息转发】发送消息时遇到错误:{}", o.getEmitterId(), e);
            ackRequest.sendAckData(MsgAckDTO.error(BaseConstants.SERVER_EXCEPTION));

        }
    }


}

