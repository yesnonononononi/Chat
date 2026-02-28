package com.summit.chat.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class NotificationVO implements Serializable {
    private String id;
    private String theme;
    private String content;
}
