package com.summit.chat.model.vo;

import lombok.Data;
import java.io.Serializable;

@Data
/**
 * 群公告VO
 */
public class GroupNoticeVO implements Serializable {

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
   * 发布者昵称
   */
  private String publisherName;
  
  /**
   * 发布者头像
   */
  private String publisherAvatar;
  
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

}