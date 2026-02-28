package com.summit.chat.Enum;

import lombok.Getter;

@Getter
public enum GroupStateEnum {
    NORMAL(1),
    BANNED(0);

    private final int state;
    GroupStateEnum(int state) {
        this.state = state;
    }

}
