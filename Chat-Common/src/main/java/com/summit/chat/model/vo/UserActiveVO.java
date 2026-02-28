package com.summit.chat.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserActiveVO {
    private String userID;
    private String nickName;
    private String icon;
    private Integer active;
}
