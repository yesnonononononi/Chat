package com.summit.chat.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
@Data
public class MediaVO implements Serializable {
    private String id;
    private String emitterId;
    private String receiverId;
    private Timestamp createTime;
    private String status;
    private Timestamp endTime;
}
