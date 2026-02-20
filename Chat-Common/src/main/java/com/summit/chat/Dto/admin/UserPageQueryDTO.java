package com.summit.chat.Dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPageQueryDTO implements Serializable {
    private String nickName;
    private Integer gender;
    private Integer status;
    private String role;
}
