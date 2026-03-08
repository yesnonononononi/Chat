package com.summit.chat.model.vo.AiToolResult;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MusicVO {
    private String code;
    private String text;
    private String type;
    private String now;
    private Object data;
}
