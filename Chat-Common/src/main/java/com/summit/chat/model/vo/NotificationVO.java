package com.summit.chat.model.vo;

import java.io.Serializable;
import lombok.Data;

@Data
public class NotificationVO implements Serializable {
    private String id;
    private String theme;
    private String content;
}
