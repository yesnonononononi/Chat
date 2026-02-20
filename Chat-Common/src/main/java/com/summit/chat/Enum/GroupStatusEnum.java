package com.summit.chat.Enum;

import lombok.Getter;

@Getter
public enum GroupStatusEnum {

    /**
     * 正常
     */
    NORMAL(1),
    /**
     * 拉黑
     */
    BLACK(2),
    /**
     * 禁言
     */
    FORBIDDEN(3);

    private final Integer status;

    GroupStatusEnum(Integer status) {
        this.status = status;
    }
}
