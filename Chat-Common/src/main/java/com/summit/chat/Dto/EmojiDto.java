package com.summit.chat.Dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class EmojiDto implements Serializable {
    private String id;
    private String url;
    private String content;
    private Long categoryId;
    private Long sort;
    private Integer status;
    private String creatorId;
    private Timestamp createTime;
    private Integer width;
    private Integer height;
}
