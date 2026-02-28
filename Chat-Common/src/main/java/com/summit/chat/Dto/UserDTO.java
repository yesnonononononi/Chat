package com.summit.chat.Dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserDTO implements Serializable {
    private Long id;
    private String nickName;
    private String mobile;
    private String pw;
    private String hobby;
    private Integer gender;
    private Integer status;
    private Integer age;
    private String role;
    private Date birth;
    private String ip;
    private String icon;
    private Date birthday;
    private String thirdAuth;
    private Integer isDelete;
}