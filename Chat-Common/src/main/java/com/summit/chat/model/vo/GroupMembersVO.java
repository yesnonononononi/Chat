package com.summit.chat.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 群聊成员vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMembersVO implements Serializable {
    private Long Id;
    private String avatar;
    private String nickName;
    private Integer gender;
    private String hobby;
    private String role;
    private String userId;
    private Timestamp joinTime;
    private Integer status;
}
