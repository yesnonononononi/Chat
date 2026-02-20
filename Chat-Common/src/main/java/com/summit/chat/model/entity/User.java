package com.summit.chat.model.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String nickName;
    @NotBlank(message = "密码不能为空")
    private String pw;
    private String mobile;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp updateTime;
    private String thirdAuth;
    private Integer status;
    private String icon;
    private Integer age;
    private String hobby;
    private String ip;
    private Integer gender; // 1男,2女
    private String role; // 角色 admin/user
    private Date birth;
    //1正常 0禁用
    private Integer isDelete; //0 已注销 1 正常

}
