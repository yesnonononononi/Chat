package com.summit.chat.GlobalHandle.SocketHandler.GroupSocketHandler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Dto.MsgAckDTO;
import com.summit.chat.Enum.ChatEvent;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.model.vo.GroupMessageVO;
import com.summit.chat.service.message.GroupMessageResendCenter.GroupMsgProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GroupDataListener implements com.corundumstudio.socketio.listener.DataListener<GroupMessageVO> {
    @Autowired
    GroupMsgProcessor msgForGroupChainProcessor;

    @Override
    public void onData(SocketIOClient client, GroupMessageVO data, AckRequest ackSender) throws Exception {
        try {
            log.info("【群聊消息转发】收到来自{}的成员{}发送的消息{}", data.getGroupId(), data.getEmitterId(), data);
            String msgId = msgForGroupChainProcessor.start(data);
            log.info("【群聊消息转发】来自{}的成员{}发送的消息{}转发成功", data.getGroupId(), data.getEmitterId(), data.getMsgId());
            if (ackSender != null) {
                ackSender.sendAckData(MsgAckDTO.success(msgId));
            }
        } catch (BusinessException e) {
            ackSender.sendAckData(MsgAckDTO.error(e.getMessage()));
        } catch (Exception e) {
            log.error("【消息转发服务】消息转发遇到问题",e);
            ackSender.sendAckData(MsgAckDTO.error(BaseConstants.SERVER_EXCEPTION));
        }

    }
}
