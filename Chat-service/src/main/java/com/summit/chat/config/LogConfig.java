package com.summit.chat.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.summit.chat.GlobalHandle.Log.ClickHouseLogAppend;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class LogConfig {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void addCustomAppender() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        Logger logger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

        ClickHouseLogAppend clickHouseLogAppend = new ClickHouseLogAppend(rabbitTemplate);
        clickHouseLogAppend.setContext(loggerContext);
        clickHouseLogAppend.start();
        logger.addAppender(clickHouseLogAppend);
    }
}
