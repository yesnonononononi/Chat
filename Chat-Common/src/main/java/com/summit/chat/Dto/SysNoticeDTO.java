package com.summit.chat.Dto;

import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 系统公告DTO
 */
@Data
public class SysNoticeDTO implements Serializable {
    private Long id;
    private String msg;
    private Timestamp createTime;
    private Timestamp endTime;
    private Long publisherId;
}
