package com.summit.chat.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLinkDto {
    @NotBlank(message = "未获取到用户信息")
    private String linkID;
    private String remark;
    private Integer isFrequent;
    private String userID;


}
