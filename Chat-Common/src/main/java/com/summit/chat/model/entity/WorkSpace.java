package com.summit.chat.model.entity;

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
  private long todayNewOrder;
  private long totalOrder;
  private double todayNewIncome;
  private double totalIncome;
  private java.sql.Timestamp createTime;


}
