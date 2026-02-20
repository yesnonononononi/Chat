package com.summit.chat.Constants;

public class QueueConstants {
    public static final String MSG_PRIVATE_QUEUE_ROUTING_KEY = "msg";
    public static final String MSG_PRIVATE_EXCHANGE_NAME = "msg.direct";
    public static final String MSG_PRIVATE_QUEUE_NAME = "msg";
    public static final String MSG_PRIVATE_QUEUE_DEAD_LETTER_ROUTING_KEY = "msg.deadLetter.queue";
    public static final String MSG_PRIVATE_EXCHANGE_DEAD_LETTER_NAME = "msg.deadLetter";
    public static final String MSG_PRIVATE_QUEUE_DEAD_LETTER_NAME = "msg.deadLetter.queue";
    public static final String MSG_PRIVATE_QUEUE_UPDATE_ROUTING_KEY = "msg.update";
    public static final String MSG_PRIVATE_UPDATE_QUEUE_NAME = "msg.update";

    public static final String MSG_GROUP_QUEUE_NAME = "group.msg";
    public static final String MSG_GROUP_QUEUE_ROUTING_KEY = "group.msg";
    public static final String MSG_GROUP_EXCHANGE_NAME = "group.msg.direct";
    public static final String MSG_GROUP_EXCHANGE_DEAD_LETTER_NAME = "group.msg.dlq.direct";
    public static final String MSG_GROUP_QUEUE_DEAD_LETTER_NAME = "group.msg.dlq";
    public static final String MSG_GROUP_QUEUE_DEAD_LETTER_ROUTING_KEY = "group.msg.dlq";
}
