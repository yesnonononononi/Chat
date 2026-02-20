package com.summit.chat.Dto;

import lombok.Data;
/*
* 群聊消息dto
 */
@Data
public class GroupMessageDTO {
    private String Id;
    private Long groupId;
    private String emitterId;
    private String messageContent;
    private String sentAt;
    private String messageType;
}
