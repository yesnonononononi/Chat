package com.summit.chat.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMqConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        // 设置消息投递确认回调（ConfirmCallback）
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("【消息发送确认】消息投递成功, id: {}", correlationData != null ? correlationData.getId() : "null");
            } else {
                log.error("【消息发送确认】消息投递失败, id: {}, 原因: {}", correlationData != null ? correlationData.getId() : "null", cause);
                // TODO: 可以在此处添加重试逻辑或记录到数据库/日志文件
            }
        });

        // 设置消息回退回调（ReturnsCallback）mandatory: true
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("【消息回退警告】消息无法路由！msg: {}, exchange: {}, routingKey: {}, code: {}, text: {}",
                    returned.getMessage(), returned.getExchange(), returned.getRoutingKey(), returned.getReplyCode(), returned.getReplyText());
        });
    }
}