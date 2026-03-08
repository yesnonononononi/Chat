package com.summit.chat.model.entity.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendApply implements Serializable {
  private String id;
  private String applicantId;
  private String recipientId;
  private String applyReason;
  private Integer status;
  private java.sql.Timestamp applyTime;
  private java.sql.Timestamp handleTime;
}
