package com.summit.chat.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageWithdrawnDTO {
    private String msgId;
    private String type;
    private String emitterId;
    private String receiverId;
    private String groupId;
}
