package com.summit.chat.listener;

import com.summit.chat.Constants.QueueConstants;
import com.summit.chat.GlobalHandle.Log.ClickHouseLogAppend;
import com.summit.chat.Mapper.ClickHouse.SystemLogMapper;
import com.summit.chat.model.entity.ck.SystemLog;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class LogQueueListener {
    @Autowired
    private SystemLogMapper systemLogMapper;



    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = QueueConstants.LOG_QUEUE_NAME,
                    durable = "true"
            ),
            key = QueueConstants.LOG_QUEUE_ROUTING_KEY,
            exchange = @Exchange(
                    name = QueueConstants.LOG_EXCHANGE_NAME,
                    type = ExchangeTypes.DIRECT, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = QueueConstants.LOG_QUEUE_DEAD_LETTER_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = QueueConstants.LOG_QUEUE_DEAD_LETTER_ROUTING_KEY)
                    }

            )
    ))
    public void logConsume(List<SystemLog> logList) {
        try {
            systemLogMapper.batchInsertSysLog(logList);
            log.info("【系统日志缓存】写入日志成功,数量: {}", logList.size());
        } catch (Exception e) {
            log.error("【系统日志缓存】写入日志发生错误,已跳过, 错误: {}", e.getMessage());
            throw e;
        }

    }



    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(
                    name = QueueConstants.LOG_QUEUE_DEAD_LETTER_EXCHANGE,
                    type = ExchangeTypes.DIRECT,
                    durable = "true"
            ),
            value = @Queue(
                    name = QueueConstants.LOG_QUEUE_DEAD_LETTER_NAME,
                    durable = "true"
            ),
            key = QueueConstants.LOG_QUEUE_DEAD_LETTER_ROUTING_KEY
    ))
    public void logDlqConsume(List<SystemLog> logList) {
        log.error("【系统日志缓存】写入日志发生错误,已进入死信队列,请即使处理,即将尝试写入本地");
        ClickHouseLogAppend.writeToLocal(logList);
    }


}
