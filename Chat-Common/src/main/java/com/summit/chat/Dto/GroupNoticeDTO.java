package com.summit.chat.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 群公告DTO
 */
public class GroupNoticeDTO implements Serializable {

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