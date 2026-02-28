package com.summit.chat.service.Impl;

import com.summit.chat.Constants.AiConstants;
import com.summit.chat.Constants.MsgConstants;
import com.summit.chat.Dto.AiChatDto;
import com.summit.chat.Enum.MsgEnum;
import com.summit.chat.Enum.MsgType;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.MsgMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.GlobalIDWorker;
import com.summit.chat.Utils.MsgQueueUtil;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.vo.PrivateMessageVO;
import com.summit.chat.service.AI.AiChatService;
import com.summit.chat.service.Impl.Support.Ai.AiMemorySupport;
import com.summit.chat.service.Impl.Support.Ai.AiStreamingSupport;
import com.summit.chat.service.Impl.Support.Ai.AiValidator;
import com.summit.chat.service.msg.MsgService;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialResponse;
import dev.langchain4j.model.chat.response.PartialResponseContext;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;

@Service
@Slf4j
public class AiChatServiceImpl implements AiChatService {
    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;
    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String baseUrl;
    @Autowired
    private AiValidator aiValidator;
    @Autowired
    private AiStreamingSupport aiStreamingSupport;
    @Autowired
    private AiMemorySupport aiMemorySupport;

    @Autowired
    private MsgMapper msgMapper;

    @Override
    public Result chat(AiChatDto dto) {
        try {
            aiValidator.validateChat(dto);
            String userId = UserHolder.getUserID();

            // 获取或清除记忆
            if (Boolean.TRUE.equals(dto.getClearContext())) {
                aiMemorySupport.clearMemory(userId);
            }
            ChatMemory chatMemory = aiMemorySupport.getChatMemory(userId);

            StreamingChatModel model = OpenAiStreamingChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .modelName(dto.getModelName())
                    .timeout(Duration.ofMinutes(1))
                    .temperature(0.7)
                    .build();

            // 使用记忆上下文进行对话
            synchronized (chatMemory) {
                // 如果是新对话，添加系统提示词
                if (chatMemory.messages().isEmpty()) {
                    chatMemory.add(new SystemMessage("你是一个乐于助人的AI助手，请用简洁的语言回答用户的问题。"));
                }
                // 添加用户消息
                chatMemory.add(new UserMessage(dto.getMessage()));
                
                partialChat(model, chatMemory, userId);
            }
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【AI】聊天失败", e);
            throw e;
        }
    }

    public void partialChat(StreamingChatModel model, ChatMemory chatMemory, String userId) {
        StringBuilder answerBuilder = new StringBuilder();
        model.chat(chatMemory.messages(), new StreamingChatResponseHandler() {
            private boolean isStart = false;

            @Override
            public void onPartialResponse(PartialResponse partialResponse, PartialResponseContext context) {
                if (!isStart) {
                    aiStreamingSupport.handlePartial(AiConstants.DEFAULT_RES_START_STREAM, userId);
                    isStart = true;
                }
                String text = partialResponse.text();
                if (text != null && !text.isEmpty()) {
                    answerBuilder.append(text);
                    aiStreamingSupport.handlePartial(text, userId);
                }
            }

            @Override
            public void onCompleteResponse(ChatResponse chatResponse) {
                // 将AI回复加入记忆
                synchronized (chatMemory) {
                    chatMemory.add(chatResponse.aiMessage());
                }

                // 兜底：如果流式过程中没有任何片段，这里补发一次完整内容，保证前端能看到回复
                if (answerBuilder.isEmpty() && chatResponse.aiMessage() != null) {
                    String fullText = chatResponse.aiMessage().text();
                    if (fullText != null && !fullText.isEmpty()) {
                        answerBuilder.append(fullText);
                        aiStreamingSupport.handlePartial(fullText, userId);
                    }
                }

                aiStreamingSupport.handlePartial(AiConstants.DEFAULT_END_STREAM, userId);
                log.info("【AI】回复完成,用户id:{}", userId);

                // 持久化 AI 回复消息
                buildMsg(answerBuilder.toString(), userId);
            }

            @Override
            public void onError(Throwable throwable) {
                // 发生错误时，移除最后一条用户消息，避免上下文错乱
                aiStreamingSupport.handlePartial(AiConstants.DEFAULT_ERROR_START_STREAM, userId);
                aiStreamingSupport.handleErr(throwable);
            }
        });
    }

    private void buildMsg(String msg,String userId){
        log.info("【AI】开始持久化回复内容，userId:{}",userId);
        PrivateMessageVO privateMessageVO = new PrivateMessageVO();
        privateMessageVO.setMsgId(GlobalIDWorker.generateId());
        privateMessageVO.setMsg(msg);
        privateMessageVO.setSendTime(new Timestamp(System.currentTimeMillis()));
        privateMessageVO.setType(MsgType.AI.getType());
        privateMessageVO.setReceiveId(userId);
        privateMessageVO.setEmitterId(AiConstants.AI_ID);
        privateMessageVO.setStatus(MsgEnum.READ.getStatus());
        privateMessageVO.setEmitterNick(AiConstants.AI_NICK);
        msgMapper.save(
                privateMessageVO
        );
        log.info("【AI】回复内容已经持久化完成，userId:{}",userId);
    }
}
