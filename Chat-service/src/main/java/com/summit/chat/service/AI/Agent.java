package com.summit.chat.service.AI;

import dev.langchain4j.service.*;


public interface Agent {
    /**
     * 流式聊天方法（支持工具调用）
     * @param memoryId 记忆ID（用户ID），用于区分不同用户的上下文
     * @param userMessage 用户输入消息
     */
@SystemMessage(fromResource = "Prompt")
    TokenStream chat(
            @MemoryId String memoryId,
            @UserMessage String userMessage);
}
