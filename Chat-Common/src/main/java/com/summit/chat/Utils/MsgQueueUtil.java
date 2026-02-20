package com.summit.chat.Utils;

import com.summit.chat.Constants.MsgConstants;
import com.summit.chat.Constants.QueueConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class MsgQueueUtil {
    RabbitTemplate rabbitTemplate;


    public MsgQueueUtil(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMsgToExChange(Object msg, String msgId){
        String correlationId = msgId != null ? msgId : UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(correlationId);
        rabbitTemplate.convertAndSend(QueueConstants.MSG_PRIVATE_EXCHANGE_NAME, QueueConstants.MSG_PRIVATE_QUEUE_ROUTING_KEY, msg, correlationData);
        log.info("【私信消息发送】消息{}已发送至MQ，等待确认", correlationId);
    }
}
