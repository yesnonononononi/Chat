package com.summit.chat.Enum;

import lombok.Getter;

@Getter
public enum VipOrderEnum {
    WAIT_PAY(0),
    PAY_SUCCESS(1),
    PAY_FAIL(2),
    BACK(3);
    final Integer status;
    VipOrderEnum(Integer status) {
        this.status = status;
    }
}
