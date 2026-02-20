package com.summit.chat.model.entity;

import lombok.Data;

@Data
/**
 * 群聊信息表
 */
public class GroupChat {


  /**
   * 群组ID
   */
  private Long id;
  
  /**
   * 群组名称
   */
  private String groupName;
  
  /**
   * 群组描述
   */
  private String groupDescription;
  
  /**
   * 创建者ID
   */
  private Long creatorId;
  
  /**
   * 创建时间
   */
  private java.sql.Timestamp createTime;
  
  /**
   * 更新时间
   */
  private java.sql.Timestamp updateTime;
  
  /**
   * 状态
   */
  private Integer status;
  /**
   * 群成员数量
   */
  private Integer number;

  /**
   * 群头像
   */
  private String icon;

}
