package com.summit.chat.Exception;

public class RequestHasHandled extends RuntimeException {
    public RequestHasHandled() {
        super("请求已经被处理");
    }
}
