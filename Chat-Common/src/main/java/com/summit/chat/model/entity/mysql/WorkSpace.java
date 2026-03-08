package com.summit.chat.model.entity.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkSpace implements Serializable {

  private long id;
  private long todayNewUser;
  private long totalUser;
  private Long totalMsg;
  private Long todayNewMsg;
  private Integer onlineUser;
  private String date;
  private java.sql.Timestamp createTime;


}
