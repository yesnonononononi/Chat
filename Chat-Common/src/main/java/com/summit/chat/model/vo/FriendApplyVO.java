package com.summit.chat.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyVO implements Serializable {
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
