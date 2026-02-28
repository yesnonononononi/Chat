package com.summit.chat.Dto;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Data
@Getter
public class CursorPage {
    private Integer pageSize;

    private Object cursor;


    private Boolean isDesc; // 是否倒序

    private Integer status; // 状态(可选)
}
