package com.summit.chat.Utils;

import com.summit.chat.Constants.QueueConstants;
import com.summit.chat.model.vo.PrivateMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.UUID;

@Component
@Slf4j
public class MsgQueueUtil {
    private final RedisTemplate<String, String> stringRedisTemplate;
    RabbitTemplate rabbitTemplate;


    public MsgQueueUtil(RabbitTemplate rabbitTemplate, RedisTemplate<String, String> stringRedisTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void sendMsgToExChange(PrivateMessageVO msg){
        String msgId = msg.getMsgId();
        String correlationId = msgId != null ? msgId : UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(correlationId);
        String sessionId = getSessionId(msg.getEmitterId(), msg.getReceiveId());
        Integer queue = getQueue(sessionId);
        rabbitTemplate.convertAndSend(QueueConstants.MSG_PRIVATE_EXCHANGE_NAME, QueueConstants.MSG_PRIVATE_ROUTE_KEY_PREFIX+queue, msg, correlationData);
        log.info("【私信消息发送】消息{}已发送至MQ，等待确认", correlationId);
    }


    public static Integer getQueue(String sessionId){
        return (sessionId.hashCode() & 0x7FFFFFFF) % QueueConstants.MSG_PRIVATE_COUNT;
    }

    public Long getSessionSeqFromCache(String sessionId){
        return stringRedisTemplate.opsForValue().increment(QueueConstants.MSG_PRIVATE_SESSION_SEQ_NAME + sessionId,1);
    }

    public static String getSessionId(String emitterId,String receiveId){
        BigInteger bigInteger = new BigInteger(emitterId);
        BigInteger bigInteger1 = new BigInteger(receiveId);
        int i = bigInteger1.compareTo(bigInteger);
        if(i >0 ){
            return bigInteger + "_" + bigInteger1;
        }else{
            return bigInteger1 + "_" + bigInteger;
        }
    }
}
