package com.summit.chat.Constants;

public class QueueConstants {

    //--------------------------私聊----------------------------
    public static final String MSG_PRIVATE_SESSION_SEQ_NAME = "chat:msg:session.seq";
    public static final String MSG_PRIVATE_EXCHANGE_NAME = "msg.direct";
    public static final String MSG_PRIVATE_QUEUE_DEAD_LETTER_ROUTING_KEY = "msg.deadLetter.queue";
    public static final String MSG_PRIVATE_EXCHANGE_DEAD_LETTER_NAME = "msg.deadLetter";
    public static final String MSG_PRIVATE_QUEUE_DEAD_LETTER_NAME = "msg.deadLetter.queue";
    public static final String MSG_PRIVATE_QUEUE_UPDATE_ROUTING_KEY = "msg.update";
    public static final String MSG_PRIVATE_UPDATE_QUEUE_NAME = "msg.update";
    public static final String MSG_PRIVATE_PREFIX = "msg_private_queue_";
    public static final String MSG_PRIVATE_ROUTE_KEY_PREFIX = "msg.private.route_";
    public static final int MSG_PRIVATE_COUNT = 32;

    // ----------------------------群聊----------------------------

    public static final String MSG_GROUP_QUEUE_NAME = "group.msg";
    public static final String MSG_GROUP_QUEUE_ROUTING_KEY = "group.msg";
    public static final String MSG_GROUP_EXCHANGE_NAME = "group.msg.direct";
    public static final String MSG_GROUP_EXCHANGE_DEAD_LETTER_NAME = "group.msg.dlq.direct";
    public static final String MSG_GROUP_QUEUE_DEAD_LETTER_NAME = "group.msg.dlq";
    public static final String MSG_GROUP_QUEUE_DEAD_LETTER_ROUTING_KEY = "group.msg.dlq";


    //----------------------------日志----------------------------
    public static final String LOG_QUEUE_NAME = "log.queue";   // 队列名称
    public static final String LOG_EXCHANGE_NAME = "log.exchange";  // 交换机名称
    public static final String LOG_QUEUE_ROUTING_KEY = "log.queue";  // 路由键
    public static final String LOG_QUEUE_DEAD_LETTER_NAME = "log.dlx.queue"; // 死信队列名称
    public static final String LOG_QUEUE_DEAD_LETTER_ROUTING_KEY = "log.dlx.queue";  // 死信队列路由键
    public static final String LOG_QUEUE_DEAD_LETTER_EXCHANGE = "log.dlx.exchange";  // 死信交换机名称

}
