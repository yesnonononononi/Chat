package com.summit.chat.model.vo;

import lombok.Data;

@Data
public class NoticeLikeVO {
    private Long noticeId;
    private Integer like;
    private Boolean isLike;
}
