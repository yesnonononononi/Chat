package com.summit.chat.Dto;

import lombok.Data;

@Data
public class PageDTO<T> {
    private Integer page;
    private Integer pageSize;
    private T dto;
}
