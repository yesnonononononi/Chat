package com.summit.chat.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 群组成员信息dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberDTO implements Serializable {
    private Long id;
    private Long userId;
    private Long memberId;
    private Long groupId;
    private Integer status;
    private String role;
}
