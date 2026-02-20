package com.summit.chat.model.vo;

import lombok.Data;

@Data
public class UserLinkVO {
    private String icon;
    private String linkId;
    private String remark;
    private String nickName;
    private Integer isDelete;
    private Integer isFrequent;
}
