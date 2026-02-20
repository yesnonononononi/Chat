package com.summit.chat.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto implements Serializable {
    private String id;
    private String applyReason;
    @NotBlank(message = "非法参数,信息不全")

    private String recipientId;

    @NotBlank(message = "非法参数,申请人信息不全")
    private String applicantId;
}