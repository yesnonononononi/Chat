package com.summit.chat.Dto;

import lombok.Data;

@Data
public class UserPwPutDto {
    private String id;
    private String pw;
    private String oldPw;
    private String mobile;
    private String verifyCode;
}
