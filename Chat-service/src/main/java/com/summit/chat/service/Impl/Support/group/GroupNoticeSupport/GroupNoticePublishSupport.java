package com.summit.chat.service.Impl.Support.group.GroupNoticeSupport;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupNoticePublishSupport {
    @Autowired
    RabbitTemplate rabbitTemplate;
}
