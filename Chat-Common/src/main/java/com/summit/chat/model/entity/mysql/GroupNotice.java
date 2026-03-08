package com.summit.chat.model.entity.mysql;

import lombok.Data;

import java.io.Serializable;

/**
 * 群公告表
 */
@Data
public class GroupNotice implements Serializable {

  /**
   * 公告ID
   */
  private Long id;
  
  /**
   * 群组ID
   */
  private Long groupId;
  
  /**
   * 发布者ID
   */
  private Long publisherId;
  
  /**
   * 公告内容
   */
  private String content;
  
  /**
   * 创建时间
   */
  private java.sql.Timestamp createTime;
  
  /**
   * 更新时间
   */
  private java.sql.Timestamp updateTime;
  
  /**
   * 是否删除 0-正常 1-删除
   */
  private Integer isDeleted;

}
