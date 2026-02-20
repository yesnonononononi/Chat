package com.summit.chat.Dto;

import com.summit.chat.Enum.MsgEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 客户端消息接收者发来的确认
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgACKOfClientDTO implements Serializable {
    private MsgEnum code;
    private String description;
}
