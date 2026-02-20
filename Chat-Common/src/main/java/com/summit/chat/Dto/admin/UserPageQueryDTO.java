package com.summit.chat.Dto.admin;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPageQueryDTO implements Serializable {
    private String nickName;
    private Integer gender;
    private Integer status;
    private String role;
}
