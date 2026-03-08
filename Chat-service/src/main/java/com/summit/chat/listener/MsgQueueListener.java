package com.summit.chat.listener;

import com.summit.chat.Constants.QueueConstants;
import com.summit.chat.Mapper.Mysql.MsgMapper;
import com.summit.chat.model.vo.PrivateMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MsgQueueListener {

    private final MsgMapper msgMapper;


    public MsgQueueListener(MsgMapper msgMapper) {
        this.msgMapper = msgMapper;
    }

    // ====================== 私信持久化 32队列消费 ======================
    @RabbitListener(queues = {
            "msg_private_queue_0", "msg_private_queue_1", "msg_private_queue_2", "msg_private_queue_3",
            "msg_private_queue_4", "msg_private_queue_5", "msg_private_queue_6", "msg_private_queue_7",
            "msg_private_queue_8", "msg_private_queue_9", "msg_private_queue_10", "msg_private_queue_11",
            "msg_private_queue_12", "msg_private_queue_13", "msg_private_queue_14", "msg_private_queue_15",
            "msg_private_queue_16", "msg_private_queue_17", "msg_private_queue_18", "msg_private_queue_19",
            "msg_private_queue_20", "msg_private_queue_21", "msg_private_queue_22", "msg_private_queue_23",
            "msg_private_queue_24", "msg_private_queue_25", "msg_private_queue_26", "msg_private_queue_27",
            "msg_private_queue_28", "msg_private_queue_29", "msg_private_queue_30", "msg_private_queue_31"
    })
    public void consumer(PrivateMessageVO msg,@Header(AmqpHeaders.CONSUMER_QUEUE) String currentQueueName) {
        log.info("【用户消息持久化】队列:{} 收到消息，发送者：{}，接收者：{}", currentQueueName, msg.getEmitterId(), msg.getReceiveId());
        try {
            msgMapper.save(msg);
            log.info("【用户消息持久化】队列:{},消息持久化完成", currentQueueName);
        } catch (Exception e) {
            log.error("【用户消息持久化】消息持久化失败", e);
            throw e;
        }
    }

    // ====================== 消息更新消费 ======================
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = QueueConstants.MSG_PRIVATE_EXCHANGE_NAME, durable = "true"),
            value = @Queue(
                    name = QueueConstants.MSG_PRIVATE_UPDATE_QUEUE_NAME, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = QueueConstants.MSG_PRIVATE_EXCHANGE_DEAD_LETTER_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = QueueConstants.MSG_PRIVATE_QUEUE_DEAD_LETTER_ROUTING_KEY)
                    }
            ),
            key = QueueConstants.MSG_PRIVATE_QUEUE_UPDATE_ROUTING_KEY
    ))
    public void consumerOfUpdate(PrivateMessageVO msg) {
        log.info("【用户消息更新】收到消息，发送者：{}，接收者：{}", msg.getEmitterId(), msg.getReceiveId());
        try {
            msgMapper.readMsgFromUser(msg.getEmitterId(), msg.getReceiveId());
            log.info("【用户消息更新】消息更新完成");
        } catch (Exception e) {
            log.error("【用户消息更新】消息更新失败", e);
            throw e;
        }
    }

    // ====================== 死信消费 ======================
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = QueueConstants.MSG_PRIVATE_EXCHANGE_DEAD_LETTER_NAME, durable = "true"),
            value = @Queue(name = QueueConstants.MSG_PRIVATE_QUEUE_DEAD_LETTER_NAME, durable = "true"),
            key = QueueConstants.MSG_PRIVATE_QUEUE_DEAD_LETTER_ROUTING_KEY
    ))
    public void msgDlqConsume(PrivateMessageVO msg) {
        log.error("【死信】消息持久化失败，发送者：{}，接收者：{}", msg.getEmitterId(), msg.getReceiveId());
    }
}