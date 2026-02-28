package com.summit.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 群组成员表
 */
public class GroupMembers implements Serializable {


  /**
   * 主键ID
   */
  private long id;
  
  /**
   * 群组ID
   */
  private long groupId;
  
  /**
   * 用户ID
   */
  private long userId;
  
  /**
   * 加入时间
   */
  private java.sql.Timestamp joinTime;
  
  /**
   * 在群组中的角色
   */
  private String role;

  /**
   * 群组状态,//'1-正常 2-拉黑 3-禁言 4-待批准';
   */
  private Integer status;

}
