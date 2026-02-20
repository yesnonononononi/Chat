package com.summit.chat.Enum;

public enum ThirdAuth {


    WECHAT("微信"),
    QQ("QQ"),
    PHONE("手机号");

    private final String displayName;

    ThirdAuth(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

