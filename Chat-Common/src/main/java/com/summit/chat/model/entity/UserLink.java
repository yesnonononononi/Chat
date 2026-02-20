package com.summit.chat.model.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLink {
    @NotNull
    private long userId;  //用户id
    @NotNull
    private long linkUserId; //好友id
    private long isFrequent; //标记是否特别关心
    private String remark; //备注
    private long isDelete; //是否删除
}
