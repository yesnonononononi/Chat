package com.summit.chat.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
* 群聊消息dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageDTO implements Serializable {
    private String Id;
    private Long groupId;
    private String emitterId;
    private String messageContent;
    private String sentAt;
    private String type;
}
