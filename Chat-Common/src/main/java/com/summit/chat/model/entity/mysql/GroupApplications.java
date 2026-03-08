package com.summit.chat.model.entity.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 群申请
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupApplications implements Serializable {

  /**
   * 主键ID
   */
  private long id;
  /**
   * 群组ID
   */
  private long groupId;
  /**
   * 申请人ID
   */
  private long applicantId;
  /**
   * 申请理由
   */
  private String applicationReason;
  /**
   * 申请状态
   */
  private String status;
  /**
   * 创建时间
   */
  private java.sql.Timestamp createdAt;
  /**
   * 更新时间
   */
  private java.sql.Timestamp updatedAt;
  /**
   * 处理人ID
   */
  private long processedBy;
  /**
   * 处理时间
   */
  private java.sql.Timestamp processedAt;
  /**
   * 拒绝理由
   */
  private String rejectionReason;


}
