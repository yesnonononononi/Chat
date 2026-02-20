package com.summit.chat.Dto;

import com.summit.chat.Enum.MsgEnum;
import lombok.Data;


/**
 * 客户端消息接收者发来的确认
 */
@Data
public class MsgACKOfClientDTO {
    private MsgEnum code;
    private String description;
}
