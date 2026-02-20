package com.summit.chat.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MediaTokenDto {
    @NotBlank(message = "房间名不能为空")
    private String roomName;
    @NotBlank(message = "用户id不能为空")
    private String userId;
    @NotBlank(message = "用户名不能为空")
    private String userName;
}
