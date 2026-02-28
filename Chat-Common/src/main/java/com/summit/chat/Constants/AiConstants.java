package com.summit.chat.Constants;

import jakarta.validation.constraints.NotBlank;

public class AiConstants {
    public static final String MODEL_NOT_EXIST =" 模型不存在";
    public static final String DEFAULT_RES_START_STREAM = "RESPONSE";
    public static final String DEFAULT_THINK_START_STREAM = "THINK";
    public static final String DEFAULT_END_STREAM = "END";
    public static final String DEFAULT_ERROR_START_STREAM = "ERROR";
    public static final String AI_ID = "8888888888888888888";
    public static final @NotBlank(message = "消息发送者昵称不能为空") String AI_NICK = "智能聊天机器人";
}
