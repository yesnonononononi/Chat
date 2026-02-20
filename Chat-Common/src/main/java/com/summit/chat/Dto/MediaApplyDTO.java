package com.summit.chat.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaApplyDTO {
    private String userId; // 这个字段在后端会用来存发起者的ID（从Token获取后填入），但在接收前端参数时，我们需要 receiverId
    private String receiverId;
    private String nickName;
    private String icon;
}
