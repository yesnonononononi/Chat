package com.summit.chat.Dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPwPutDto implements Serializable {
    private String id;
    private String pw;
    private String oldPw;
    private String mobile;
    private String verifyCode;
}
