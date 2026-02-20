package com.summit.chat.listener;

import com.summit.chat.Constants.QueueConstants;
import com.summit.chat.model.vo.GroupMessageVO;
import com.summit.chat.service.group.GroupMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GroupMsgQueueListener {
    @Autowired
    private GroupMessageService groupMessageService;

    /**
     * 群聊消息持久化消费
     * 优化：1. 引用常量 2. 业务交换机补全durable=true 3. 保留多路由键（msg/msg_notice）
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(
                    name = QueueConstants.MSG_GROUP_EXCHANGE_NAME,
                    type = ExchangeTypes.DIRECT,
                    durable = "true" // 补全持久化，生产环境必备
            ),
            value = @Queue(
                    name = QueueConstants.MSG_GROUP_QUEUE_NAME,
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = QueueConstants.MSG_GROUP_EXCHANGE_DEAD_LETTER_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = QueueConstants.MSG_GROUP_QUEUE_DEAD_LETTER_ROUTING_KEY)
                    }
            ),
            key = {QueueConstants.MSG_GROUP_QUEUE_ROUTING_KEY, "msg_notice"} // 保留业务多路由键
    ))
    public void consumer(GroupMessageVO msg) {
        log.info("【群聊消息持久化】收到消息:{}，发送者：{}，群聊：{}", msg, msg.getEmitterId(), msg.getGroupId());
        try {
            groupMessageService.addGroupMsg(msg);
        } catch (Exception e) {
            log.error("【群聊消息持久化】消息:{} 持久化失败,发送者:{},群聊:{}", msg, msg.getEmitterId(), msg.getGroupId(), e);
            throw e; // 抛出异常触发重试，重试失败后入死信
        }
        log.info("【群聊消息持久化】消息{}持久化完成", msg);

    }

    /**
     * 群聊消息死信消费
     * 优化：1. 引用常量 2. 日志级别改为ERROR（更醒目）
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(
                    name = QueueConstants.MSG_GROUP_EXCHANGE_DEAD_LETTER_NAME,
                    type = ExchangeTypes.DIRECT,
                    durable = "true"
            ),
            value = @Queue(
                    name = QueueConstants.MSG_GROUP_QUEUE_DEAD_LETTER_NAME,
                    durable = "true"
            ),
            key = QueueConstants.MSG_GROUP_QUEUE_DEAD_LETTER_ROUTING_KEY
    ))
    public void msgDlqConsume(GroupMessageVO msg) {
        log.error("【群聊消息持久化】消息持久化失败，发送者：{}，群聊：{}，已进入死信队列，请及时处理！消息内容：{}",
                msg.getEmitterId(), msg.getGroupId(), msg);
    }
}