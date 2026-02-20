package com.summit.chat.model.entity;

import com.summit.chat.Enum.GroupStatusEnum;
import lombok.Data;

@Data
/**
 * 群组成员表
 */
public class GroupMembers {


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
