package com.summit.chat.Controller.user;


import com.summit.chat.Annotation.ShakeProtect;
import com.summit.chat.Dto.AiChatDto;
import com.summit.chat.Result.Result;
import com.summit.chat.service.AI.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ai")
@Tag(name = "langchain4j")
public class ChatController {

    @Autowired
    private AiChatService aiChatService;

    @PostMapping("/chat")
    @ShakeProtect(value = "T(com.summit.chat.Utils.UserHolder).getUserID()")
    @Operation(summary = "聊天")
    public Result chat(@RequestBody AiChatDto dto) {
        return aiChatService.chat(dto);
    }
}
