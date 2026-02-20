package com.summit.chat.Enum;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    USER("user"),
    ADMIN("admin"),
    SUPER_ADMIN("super_admin");
    private final String role;
    UserRoleEnum(String role) {
        this.role = role;
    }
}
