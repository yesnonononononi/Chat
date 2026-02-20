package com.summit.chat.model.entity;

import lombok.Data;

@Data
public class FriendApply {
  private String id;
  private String applicantId;
  private String recipientId;
  private String applyReason;
  private Integer status;
  private java.sql.Timestamp applyTime;
  private java.sql.Timestamp handleTime;
}
