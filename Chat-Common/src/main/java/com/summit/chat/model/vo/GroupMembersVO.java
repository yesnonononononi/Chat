package com.summit.chat.model.vo;

import com.summit.chat.Enum.GroupStatusEnum;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 群聊成员vo
 */
@Data
public class GroupMembersVO {
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
