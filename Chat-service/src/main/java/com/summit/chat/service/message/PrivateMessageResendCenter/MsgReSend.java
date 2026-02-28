package com.summit.chat.service.message.PrivateMessageResendCenter;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Dto.MsgAckDTO;
import com.summit.chat.Enum.ChatEvent;
import com.summit.chat.Enum.MsgEnum;
import com.summit.chat.model.vo.MsgCallbackForPrivateVO;
import com.summit.chat.model.vo.PrivateMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MsgReSend {

    public void conduct(MsgContext<PrivateMessageVO> context) {

        PrivateMessageVO msg = context.getMsg();
        SocketIOClient emitterClient = context.getEmitterClient();
        String msgId = msg.getMsgId();
        //发送消息确认,返回生成的消息id给发送者
        ackToEmitterWithMsgId(context.getAckRequest(), msgId, msg.getEmitterId(), msg.getSendTime().getTime());
        // 确保状态绝对不为null，如果为null则设为未读(2)
        if (!context.isOnline()) {
            resendToEmitter(MsgEnum.NOT_ONLINE.getStatus(), msgId, emitterClient, null, null);
            return;
        }

        //用户在线，进行转发
        SocketIOClient receiveClient = context.getReceiveClient();
        resendToReceiver(receiveClient, emitterClient, msg);
        //更新消息状态为已读
        msg.setStatus(MsgEnum.READ.getStatus());
    }


    /**
     * 转发消息给发送者
     *
     * @param msgCode       消息状态码
     * @param msgId         消息id
     * @param emitterClient 发送者客户端
     * @param description   额外描述
     * @param symbol        冗余标志
     */
    private void resendToEmitter(Integer msgCode, String msgId, SocketIOClient emitterClient, @Nullable String description, @Nullable String symbol) {

        MsgCallbackForPrivateVO msg = MsgCallbackForPrivateVO
                .builder()
                .msgCode(msgCode)
                .symbol(symbol)
                .msgId(msgId)
                .description(description)
                .build();

        emitterClient.sendEvent(ChatEvent.CHAT_DELIVERED.getType(), msg);

    }


    /**
     * 转发消息给接收者
     *
     * @param receiveClient 接收者客户端
     * @param vo            消息
     */
    private void resendToReceiver(SocketIOClient receiveClient, SocketIOClient emitterClient, PrivateMessageVO vo) {
        receiveClient.sendEvent(ChatEvent.PRIVATE_MSG_RECEIVE.getType(), ack(emitterClient), vo);
    }


    private AckCallback<MsgCallbackForPrivateVO> ack(SocketIOClient emitterClient) {
        return new AckCallback<>(MsgCallbackForPrivateVO.class) {
            @Override
            public void onSuccess(MsgCallbackForPrivateVO result) {
                if (result != null) {
                    String msgId = result.getMsgId();
                    log.info("【私聊消息确认】收到客户端消息{}已读确认", msgId);
                    if (result.getMsgCode().equals(MsgEnum.READ.getStatus())) {
                        //转发消息给发送者
                        resendToEmitter(MsgEnum.READ.getStatus(), msgId, emitterClient, null, null);
                        log.info(" 【私聊消息确认】消息{}状态提示已转发给发送者", msgId);
                    }
                }
            }
        };


    }

    private void ackToEmitterWithMsgId(AckRequest ackRequest, String msgId,String emitterId, Long sendTime) {
        if (ackRequest != null) {
            log.info("【消息回执】将生成的消息id{}返回给发送者{}",msgId,emitterId);
            ackRequest.sendAckData(MsgAckDTO.success(msgId, sendTime));
        }
    }

}

