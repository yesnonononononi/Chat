package com.summit.chat.model.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class EmojiCategoryVO implements Serializable {
    private long id;
    private String name;
    private Integer sort;
    private java.sql.Timestamp createTime;
    private Integer status;
}
