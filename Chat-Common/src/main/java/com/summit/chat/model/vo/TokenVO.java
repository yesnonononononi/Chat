package com.summit.chat.model.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class TokenVO implements Serializable {
    private String token;
    private String expiration;
    private UserVO userVO;
}
