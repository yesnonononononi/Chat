package com.summit.chat.model.vo;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class MediaVO {
    private String id;
    private String emitterId;
    private String receiverId;
    private Timestamp createTime;
    private String status;
    private Timestamp endTime;
}
