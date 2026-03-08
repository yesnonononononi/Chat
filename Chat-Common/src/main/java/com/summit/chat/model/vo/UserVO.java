package com.summit.chat.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserVO implements Serializable {
    private String id;
    private String nickName;
    private String mobile;
    private String hobby;
    private Integer gender;
    private Integer status;
    private Integer age;
    private String role;
    private Date birth;
    private String ip;
    private String icon;
    private Integer isDelete;
    private String createTime;
}
