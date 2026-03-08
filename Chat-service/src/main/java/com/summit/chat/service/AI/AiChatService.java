package com.summit.chat.service.AI;

import com.summit.chat.Dto.AiChatDto;
import com.summit.chat.Result.Result;


public interface AiChatService  {
    public Result chat( AiChatDto dto);
}
