package com.summit.chat.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyCodeLoginRequest {
    @NotBlank(message = "手机号为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误（需为11位有效手机号）")
    private String mobile;
    @NotBlank(message = "验证码为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码格式错误（需为6位数字）")
    private String verifyCode;
}
