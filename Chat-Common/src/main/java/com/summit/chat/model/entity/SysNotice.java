package com.summit.chat.model.entity;

import lombok.Data;

@Data
public class SysNotice {

  private Long id;
  private String msg;
  private java.sql.Timestamp createTime;
  private java.sql.Timestamp endTime;
  private Long version;
  private Long isDeleted;
  private Long publisherId;
  private Long like;
  private boolean isLike;


}
