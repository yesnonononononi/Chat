package com.summit.chat.listener;

import com.summit.chat.Constants.QueueConstants;
import com.summit.chat.Mapper.MsgMapper;
import com.summit.chat.model.vo.PrivateMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MsgQueueListener {

    private final RabbitTemplate template;
    private final MsgMapper msgMapper;

    // 构造器注入（推荐，无需@Autowired）
    public MsgQueueListener(RabbitTemplate template, MsgMapper msgMapper) {
        this.template = template;
        this.msgMapper = msgMapper;
    }

    /**
     * 私信消息持久化消费
     * 优化：1. 引用常量 2. 业务交换机补全durable=true 3. 保留多路由键（msg/msg_notice）
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(
                    name = QueueConstants.MSG_PRIVATE_EXCHANGE_NAME,
                    type = ExchangeTypes.DIRECT,
                    durable = "true" // 补全持久化，生产环境必备
            ),
            value = @Queue(
                    name = QueueConstants.MSG_PRIVATE_QUEUE_NAME,
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = QueueConstants.MSG_PRIVATE_EXCHANGE_DEAD_LETTER_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = QueueConstants.MSG_PRIVATE_QUEUE_DEAD_LETTER_ROUTING_KEY)
                    }
            ),
            key = {QueueConstants.MSG_PRIVATE_QUEUE_ROUTING_KEY, "msg_notice"} // 保留业务多路由键
    ))
    public void consumer(PrivateMessageVO msg) {
        log.info("【用户消息持久化】收到消息:{}，发送者：{}，接收者：{}", msg, msg.getEmitterId(), msg.getReceiveId());
        try {
            msgMapper.save(msg);
        } catch (Exception e) {
            log.error("【用户消息持久化】消息:{} 持久化失败,发送者:{},接收者:{}", msg, msg.getEmitterId(), msg.getReceiveId(), e);
            throw e; // 抛出异常触发重试，重试失败后入死信
        }
        log.info("【用户消息持久化】消息{}持久化完成", msg);
    }

    /**
     * 私信消息更新消费
     * @param msg
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(
                    name = QueueConstants.MSG_PRIVATE_EXCHANGE_NAME,
                    type = ExchangeTypes.DIRECT,
                    durable = "true"
            ),
            value = @Queue(
                    name = QueueConstants.MSG_PRIVATE_UPDATE_QUEUE_NAME,
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = QueueConstants.MSG_PRIVATE_EXCHANGE_DEAD_LETTER_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = QueueConstants.MSG_PRIVATE_QUEUE_DEAD_LETTER_ROUTING_KEY)
                    }
            ),
            key = QueueConstants.MSG_PRIVATE_QUEUE_UPDATE_ROUTING_KEY
    ))
    public void consumerOfUpdate(PrivateMessageVO msg){
        try {
            log.info("【用户消息更新】收到消息:{}，发送者：{}，接收者：{}", msg, msg.getEmitterId(), msg.getReceiveId());
            msgMapper.readMsgFromUser(msg.getEmitterId(), msg.getReceiveId());
            log.info("【用户消息更新】消息{}更新完成", msg);
        }catch (Exception e) {
            log.error("【用户消息更新】消息:{} 更新失败,发送者:{},接收者:{}", msg, msg.getEmitterId(), msg.getReceiveId(), e);
            throw e; // 抛出异常触发重试，重试失败后入死信
        }
    }

    /**
     * 私信消息死信消费
     * 优化：1. 引用常量 2. 日志级别改为ERROR（更醒目）
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(
                    name = QueueConstants.MSG_PRIVATE_EXCHANGE_DEAD_LETTER_NAME,
                    type = ExchangeTypes.DIRECT,
                    durable = "true"
            ),
            value = @Queue(
                    name = QueueConstants.MSG_PRIVATE_QUEUE_DEAD_LETTER_NAME,
                    durable = "true"
            ),
            key = QueueConstants.MSG_PRIVATE_QUEUE_DEAD_LETTER_ROUTING_KEY
    ))
    public void msgDlqConsume(PrivateMessageVO msg) {
        log.error("【用户消息持久化】消息持久化失败，发送者：{}，接收者：{}，已进入死信队列，请及时处理！消息内容：{}",
                msg.getEmitterId(), msg.getReceiveId(), msg);
    }
}