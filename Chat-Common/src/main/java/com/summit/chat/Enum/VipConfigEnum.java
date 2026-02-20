package com.summit.chat.Enum;

import lombok.Getter;

@Getter
public enum VipConfigEnum {
    NORMAL(1),
    DELETE(2),
    STOP(3);
    final Integer status;
    VipConfigEnum(int i) {
        this.status = i;
    }
}
