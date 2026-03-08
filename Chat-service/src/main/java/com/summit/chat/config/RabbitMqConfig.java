package com.summit.chat.config;

import com.summit.chat.Constants.QueueConstants;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class RabbitMqConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        // 消息发送成功失败回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("【消息发送确认】成功, id: {}", correlationData != null ? correlationData.getId() : "null");
            } else {
                log.error("【消息发送确认】失败, id: {}, 原因: {}", correlationData != null ? correlationData.getId() : "null", cause);
            }
        });

        // 消息无法路由回调
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("【消息无法路由】exchange:{}, routingKey:{}", returned.getExchange(), returned.getRoutingKey());
        });
    }

    // =====================================
    // 1. 业务交换机
    // =====================================
    @Bean
    public Exchange privateMsgExchange() {
        return ExchangeBuilder.directExchange(QueueConstants.MSG_PRIVATE_EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    // =====================================
    // 2. 死信交换机
    // =====================================
    @Bean
    public Exchange privateDeadMsgExchange() {
        return ExchangeBuilder.directExchange(QueueConstants.MSG_PRIVATE_EXCHANGE_DEAD_LETTER_NAME)
                .durable(true)
                .build();
    }

    // =====================================
    // 3. 死信队列
    // =====================================
    @Bean
    public Queue privateMsgQueueDeadLetter() {
        return QueueBuilder.durable(QueueConstants.MSG_PRIVATE_QUEUE_DEAD_LETTER_NAME).build();
    }

    // =====================================
    // 4. 死信队列绑定死信交换机
    // =====================================
    @Bean
    public Binding privateDeadLetterBinding() {
        return BindingBuilder.bind(privateMsgQueueDeadLetter())
                .to(privateDeadMsgExchange())
                .with(QueueConstants.MSG_PRIVATE_QUEUE_DEAD_LETTER_ROUTING_KEY)
                .noargs();
    }

    // =====================================
    // 5. 核心：创建 32 个业务队列 + 绑定
    // =====================================
    @Bean
    public RabbitAdmin privateMsgQueue(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        for (int i = 0; i < QueueConstants.MSG_PRIVATE_COUNT; i++) {

            // 队列名：msg_private_queue_0、1、2...
            String queueName = QueueConstants.MSG_PRIVATE_PREFIX + i;
            // 路由键：msg.private.route_0、1、2...
            String routingKey = QueueConstants.MSG_PRIVATE_ROUTE_KEY_PREFIX + i;
            // 队列（带死信）
            Queue queue = QueueBuilder.durable(queueName)
                    .withArgument("x-dead-letter-exchange", QueueConstants.MSG_PRIVATE_EXCHANGE_DEAD_LETTER_NAME)
                    // 死信路由键 统一用死信的，不是业务 routingKey！
                    .withArgument("x-dead-letter-routing-key", QueueConstants.MSG_PRIVATE_QUEUE_DEAD_LETTER_ROUTING_KEY)
                    .build();
            rabbitAdmin.declareQueue(queue);
            // 绑定
            Binding binding = BindingBuilder.bind(queue)
                    .to(privateMsgExchange())
                    .with(routingKey)
                    .noargs();
            rabbitAdmin.declareBinding(binding);
        }
        return rabbitAdmin;
    }



}