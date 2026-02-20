package com.summit.chat.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NormalLoginRequest {
    @NotBlank(message = "手机号为空")
    public String mobile;
    @NotBlank(message = "密码为空")
    public String pw;
}
