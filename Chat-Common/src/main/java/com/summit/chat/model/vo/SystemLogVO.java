package com.summit.chat.model.vo;


import lombok.Data;

@Data
public class SystemLogVO {
    private String id;
    private String content;
    private String level;
    private String createTime;
}
