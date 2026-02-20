package com.summit.chat.Enum;

import lombok.Getter;

@Getter
public enum GroupApplicationEnum {
    PENDING("pending"),
    APPROVE("approved"),
    REJECT("rejected");
    private final String status;
    GroupApplicationEnum(String status) {
        this.status = status;
    }

}
