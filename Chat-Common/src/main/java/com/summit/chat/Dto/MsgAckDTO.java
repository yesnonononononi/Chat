package com.summit.chat.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgAckDTO implements Serializable {
    private boolean isSuccess;
    private String msgId;
    private Long sendTime;
    private String errorMsg;

    public static MsgAckDTO success(String msgId, Long sendTime) {
        return new MsgAckDTO(true, msgId, sendTime, null);
    }

    public static MsgAckDTO error(String errorMsg) {
        return new MsgAckDTO(false, null, null, errorMsg);
    }
}
