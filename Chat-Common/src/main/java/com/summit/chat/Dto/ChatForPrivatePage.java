package com.summit.chat.Dto;

import com.summit.chat.model.vo.PrivateMessageVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatForPrivatePage implements Serializable {
    private PrivateMessageVO dto;
    private Integer page;
    private Integer pageSize;
}

