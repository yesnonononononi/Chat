package com.summit.chat.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 系统公告VO
 */
@Data
public class SysNoticeVO implements Serializable {
    private Long id;
    private String msg;
    private Timestamp createTime;
    private Timestamp endTime;
    private Long publisherId;
    private String publisherName;
}
