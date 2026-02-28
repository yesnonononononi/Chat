package com.summit.chat.service.message.GroupMessageResendCenter;

import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Constants.QueueConstants;
import com.summit.chat.Enum.ChatEvent;
import com.summit.chat.model.vo.GroupMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.UUID;

@Slf4j
@Component
public class GroupMsgResendAssessor {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void conduct(GroupMsgContext context) {
        //转发消息给所有群聊之中的人,此处不添加已读未读功能
        GroupMessageVO msg = context.getMsg();
        try {
            //如果不在线,直接持久化,即跳过
            HashSet<SocketIOClient> clients = context.getClients();
            //在线推送
            clients.forEach(client -> {
                client.sendEvent(ChatEvent.GROUP_MSG_RECEIVE.getType(), msg);
            });
            //持久化
            // 使用消息ID作为CorrelationData的ID，以便在回调中追踪
            String correlationId = msg.getMsgId() != null ? String.valueOf(msg.getMsgId()) : UUID.randomUUID().toString();
            CorrelationData correlationData = new CorrelationData(correlationId);
            
            rabbitTemplate.convertAndSend(QueueConstants.MSG_GROUP_EXCHANGE_NAME,
                    QueueConstants.MSG_GROUP_QUEUE_ROUTING_KEY,
                    msg,
                    correlationData);
            
            log.info("【群聊消息转发】群聊{}消息{}已发送至MQ，等待确认",msg.getMsgId(),msg.getGroupId());
        }catch (Exception e){
            log.error("【群聊消息转发】群聊{}消息{}转发失败",msg.getMsgId(),msg.getGroupId());
            throw e;
        }
    }
}
