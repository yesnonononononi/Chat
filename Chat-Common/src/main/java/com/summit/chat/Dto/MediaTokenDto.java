package com.summit.chat.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaTokenDto implements Serializable {
    @NotBlank(message = "房间名不能为空")
    private String roomName;
    @NotBlank(message = "用户id不能为空")
    private String userId;
    @NotBlank(message = "用户名不能为空")
    private String userName;
}
