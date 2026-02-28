package com.summit.chat.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmojiVO implements Serializable {
    private Long id;
    private String url;
    private String content;
    private Integer width;
    private Integer height;
    private String categoryName;
    private String creatorName;
    private java.sql.Timestamp createTime;
    private Integer status;
}
