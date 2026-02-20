package com.summit.chat.Dto;

import com.summit.chat.model.vo.PrivateMessageVO;
import lombok.Data;

@Data
public class ChatForPrivatePage {
    private PrivateMessageVO dto;
    private Integer page;
    private Integer pageSize;
}

