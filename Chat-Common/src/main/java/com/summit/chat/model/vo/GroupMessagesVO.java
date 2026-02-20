package com.summit.chat.model.vo;

import lombok.Data;
/**
 * 群聊信息表vo
 */
@Data
public class GroupMessagesVO {
    private Long id;
    private Long groupId;
    private Long senderNickName;
    private String content;
    private java.sql.Timestamp createTime;
    private String messageType;
}
