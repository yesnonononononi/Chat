package com.summit.chat.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 群聊申请DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupApplicationDTO implements Serializable {
    /**
     * 申请ID
     */
    private Long id;

    /**
     * 群组ID
     */
    private Long groupId;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请理由
     */
    private String applicationReason;

    /**
     * 拒绝理由
     */
    private String rejectionReason;

    /**
     * 处理人ID
     */
    private Long processedBy;
}
