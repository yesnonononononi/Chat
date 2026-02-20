package com.summit.chat.model.vo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 群聊申请VO
 */
@Data
public class GroupApplicationVO {
    /**
     * 申请ID
     */
    private String id; // 前端通常使用String避免精度丢失

    /**
     * 群组ID
     */
    private String groupId;
    
    /**
     * 群组名称
     */
    private String groupName;

    /**
     * 群组头像
     */
    private String groupIcon;

    /**
     * 申请人ID
     */
    private String applicantId;

    /**
     * 申请人昵称
     */
    private String applicantNick;

    /**
     * 申请人头像
     */
    private String applicantIcon;

    /**
     * 申请理由
     */
    private String applicationReason;

    /**
     * 状态: 0-待处理, 1-已同意, 2-已拒绝
     */
    private String status;

    /**
     * 拒绝理由
     */
    private String rejectionReason;

    /**
     * 申请时间
     */
    private Timestamp createdAt;

    /**
     * 处理时间
     */
    private Timestamp processedAt;
}
