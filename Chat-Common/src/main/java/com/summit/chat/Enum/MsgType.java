package com.summit.chat.Enum;

public enum MsgType {
    //enum('text', 'image', 'file', 'system')
    TEXT("text"),
    IMAGE("image"),
    FILE("file"),
    SYSTEM("system"),
     AI ("AI");
    private final String type;
    MsgType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
}
