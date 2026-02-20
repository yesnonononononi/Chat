package com.summit.chat.Enum;

import lombok.Getter;

@Getter
public enum UserCallStateEnum {
    IDLE(1),
    RING(2),
    CALLING(3);

     final Integer state;
     UserCallStateEnum(Integer state) {
        this.state = state;
    }
}
