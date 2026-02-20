package com.summit.chat.Enum;

import lombok.Getter;

@Getter
public enum MsgEnum {
    READ(0),  //已读
    WITHDRAWN(1), //已撤回
    NOT_ONLINE(2),//未读
    FAIL(3);  //发送失败
    final Integer status;

    MsgEnum(Integer status) {
        this.status = status;
    }
}
