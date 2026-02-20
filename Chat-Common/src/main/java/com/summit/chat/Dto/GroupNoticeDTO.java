package com.summit.chat.Dto;

import lombok.Data;

@Data
/**
 * 群公告DTO
 */
public class GroupNoticeDTO {

  /**
   * 公告ID（更新时使用）
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

}