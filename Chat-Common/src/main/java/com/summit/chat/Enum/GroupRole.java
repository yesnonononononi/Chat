package com.summit.chat.Enum;

import lombok.Getter;

@Getter
public enum GroupRole {
    OWNER("owner"),
    MEMBER("member"),
    ADMIN("admin");
    private final String role;
    GroupRole(String role) {
        this.role = role;
    }
}
