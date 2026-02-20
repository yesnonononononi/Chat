package com.summit.chat.Enum;

public enum GroupStateEnum {
    NORMAL(1),
    BANNED(0),
    DELETED(2);
    private int state;
    GroupStateEnum(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
