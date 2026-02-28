package com.summit.chat.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.lang.annotation.Documented;

@Data

/**
 * 群聊消息表
 */
public class GroupMessages implements Serializable {


  @Id
  /**
   * 消息ID
   */
  private String id;
  
  /**
   * 群组ID
   */
  private long groupId;
  
  /**
   * 发送者ID
   */
  private long emitterId;
  
  /**
   * 消息内容
   */
  private String msg;
  
  /**
   * 发送时间
   */
  private java.sql.Timestamp createTime;
  
  /**
   * 消息类型
   */
  private String type;
  /**
   * 1删除,0正常
   */
  private Integer isDeleted;

}
