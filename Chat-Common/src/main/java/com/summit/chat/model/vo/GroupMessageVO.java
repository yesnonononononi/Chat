package com.summit.chat.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>消息体</p>
 */
@Data
public class GroupMessageVO implements Serializable {
    /**
     * 群id
     */
    private Long groupId;
    /**
     * 消息内容
     */
    private String msg;
    /**
     * 消息id
     */
    private String msgId;
    /**
     * 发送者id
     */
    private Long emitterId;
    /*
     * 消息是否删除 0:否 1:是
     */
    private Integer isDeleted;
    private String type;
    private Timestamp createTime;
    /**
     * 发送者昵称
     */
    private String nickName;
    /**
     * 发送者头像
     */
    private String icon;
}
