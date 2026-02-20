package com.summit.chat.Dto;

import lombok.Data;

/**
 * 群组成员信息dto
 */
@Data
public class GroupMemberDTO {
    private Long id;
    private Long userId;
    private Long memberId;
    private Long groupId;
    private Integer status;
    private String role;
}
