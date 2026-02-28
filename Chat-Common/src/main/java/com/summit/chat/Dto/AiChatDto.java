package com.summit.chat.Dto;

import lombok.Data;

@Data
public class AiChatDto {
    private String modelName;
    private String message;
    private Boolean clearContext = false;
}
