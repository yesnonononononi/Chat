package com.summit.chat.model.vo;

import lombok.Data;

@Data
public class FriendApplyVO {
    private String id;
    private String icon;
    private String applicantNick;
    private String applicantId;
    private String recipientId;
    private String applyReason;
    private Integer status;
    private java.sql.Timestamp applyTime;
    private java.sql.Timestamp handleTime;
}
