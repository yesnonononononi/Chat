package com.summit.chat.model.entity;

import lombok.Data;

@Data
public class WorkSpace {

  private long id;
  private long todayNewUser;
  private long totalUser;
  private long todayNewOrder;
  private long totalOrder;
  private double todayNewIncome;
  private double totalIncome;
  private java.sql.Timestamp createTime;


}
