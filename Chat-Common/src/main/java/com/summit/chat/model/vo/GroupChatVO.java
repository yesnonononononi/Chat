package com.summit.chat.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 群聊信息表vo
 */
@Data
public class GroupChatVO implements Serializable {
    private Long id;
    private String groupName;
    private String groupDescription;
    private Long creatorId;
    private String creatorName;
    private Timestamp createTime;
    private Integer status;
    private Integer number;
    private String icon;
}
