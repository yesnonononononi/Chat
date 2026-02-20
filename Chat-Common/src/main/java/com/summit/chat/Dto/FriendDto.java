package com.summit.chat.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FriendDto {
    private String id;
    private String applyReason;
    @NotBlank(message = "非法参数,信息不全")

    private String recipientId;

    @NotBlank(message = "非法参数,申请人信息不全")
    private String applicantId;
}