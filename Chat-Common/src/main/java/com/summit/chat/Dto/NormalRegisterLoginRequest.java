package com.summit.chat.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NormalRegisterLoginRequest {
    @NotBlank(message = "手机号为空")
    public String mobile;
    @NotBlank(message = "验证码不能为空")
    public String verifyCode;
    public String pw;
    public String nickName;
}
