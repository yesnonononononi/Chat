package com.summit.chat.model.entity;

import lombok.Data;

@Data
public class SysNotice {
  private long id;
  private String msg;
  private java.sql.Timestamp createTime;
  private java.sql.Timestamp endTime;
  private long version;
  private long isDeleted;
  private long publisherId;
}
