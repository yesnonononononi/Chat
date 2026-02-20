package com.summit.chat.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgAckDTO {
    private boolean isSuccess;
    private String msgId;
    private String errorMsg;

    public static MsgAckDTO success(String msgId) {
        return new MsgAckDTO(true, msgId, null);
    }

    public static MsgAckDTO error(String errorMsg) {
        return new MsgAckDTO(false, null, errorMsg);
    }
}
