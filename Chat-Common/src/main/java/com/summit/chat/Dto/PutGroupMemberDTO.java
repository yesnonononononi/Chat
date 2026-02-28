package com.summit.chat.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PutGroupMemberDTO implements Serializable {
    @NotNull(message = "对象id不能为空")
    private Long userId;
    @NotNull(message = "群聊id不能为空")
    private Long groupId;
    @NotNull(message = "状态不能为空")
    private Integer status;
    private Long banDuration; //禁言时长
}
