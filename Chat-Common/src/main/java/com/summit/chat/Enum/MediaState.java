package com.summit.chat.Enum;

import lombok.Getter;

@Getter
public enum MediaState {
    ACCEPT("accept"),
    REJECT("reject"),
    CANCEL("cancel"),
    WAIT("wait");
    private final String state;
    MediaState(String state) {
        this.state = state;
    }
}
