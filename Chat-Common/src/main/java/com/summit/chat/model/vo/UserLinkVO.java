package com.summit.chat.model.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserLinkVO implements Serializable {
    private String icon;
    private String linkId;
    private String remark;
    private String nickName;
    private Integer isDelete;
    private Integer isFrequent;
}
