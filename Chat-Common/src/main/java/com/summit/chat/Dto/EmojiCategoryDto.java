package com.summit.chat.Dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class EmojiCategoryDto implements Serializable {
    private long id;
    private String name;
    private Integer sort;
    private Integer status;
}
