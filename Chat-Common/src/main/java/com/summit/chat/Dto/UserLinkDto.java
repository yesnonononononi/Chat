package com.summit.chat.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLinkDto implements Serializable {
    @NotBlank(message = "未获取到用户信息")
    private String linkID;
    private String remark;
    private Integer isFrequent;
    private String userID;


}
