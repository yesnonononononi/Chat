package com.summit.chat.model.vo;

import lombok.Data;

@Data
public class TokenVO {
    private String token;
    private String expiration;
    private UserVO userVO;
}
