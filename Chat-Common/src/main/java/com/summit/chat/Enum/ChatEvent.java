package com.summit.chat.Enum;

import lombok.Getter;

@Getter
public enum ChatEvent {
    PRIVATE_MSG_SEND("chat_private_send"),
    PRIVATE_MSG_RECEIVE("chat_private_receive"),
    SYS_NOTICE("system_notice"),
    CHAT_DELIVERED("chat_delivered"), //消息回执,要加_消息id后缀
    RESEND_ERROR("chat_error") ,
    //群聊
    JOIN_GROUP("join_group"),  //入群事件
    GROUP_MSG_RECEIVE ("chat_group"), //系统->群聊
    GROUP_MSG_SEND("chat_group_receive"), //群聊消息监听
    UN_LOGIN("un_login"),
    CHAT_READ("chat_read"),// 消息已读通知


    //系统
    KICK_USER("force_logout");
    final String type;

    ChatEvent(String type) {
        this.type = type;
    }

}