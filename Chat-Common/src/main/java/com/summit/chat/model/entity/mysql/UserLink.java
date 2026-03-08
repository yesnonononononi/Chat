package com.summit.chat.model.entity.mysql;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLink implements Serializable {
    @NotNull
    private long userId;  //用户id
    @NotNull
    private long linkUserId; //好友id
    private long isFrequent; //标记是否特别关心
    private String remark; //备注
    private long isDelete; //是否删除
}
