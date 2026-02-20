package com.summit.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysNotice implements Serializable {
  private long id;
  private String msg;
  private java.sql.Timestamp createTime;
  private java.sql.Timestamp endTime;
  private long version;
  private long isDeleted;
  private long publisherId;
}
