package com.summit.chat.service.Impl;

import com.summit.chat.Constants.AiConstants;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Dto.AiChatDto;
import com.summit.chat.Enum.MsgEnum;
import com.summit.chat.Enum.MsgType;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.Mysql.MsgMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.GlobalIDWorker;
import com.summit.chat.Utils.MsgQueueUtil;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.vo.PrivateMessageVO;
import com.summit.chat.service.AI.Agent;
import com.summit.chat.service.AI.AiChatService;
import com.summit.chat.service.Impl.Support.Ai.AiStreamingSupport;
import com.summit.chat.service.Impl.Support.Ai.AiValidator;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@Slf4j
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private AiValidator aiValidator;
    @Autowired
    private AiStreamingSupport aiStreamingSupport;

    @Autowired
    private Agent agent;


    @Autowired
    private MsgMapper msgMapper;
    @Autowired
    private MsgQueueUtil msgQueueUtil;

    @Override
    public Result chat(AiChatDto dto) {
        try {
            aiValidator.validateChat(dto);
            String userId = UserHolder.getUserID();
            if(userId == null){
                return Result.fail(BaseConstants.SERVER_EXCEPTION);
            }
            partialChat(dto, userId);
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【AI】聊天失败", e);
            throw e;
        }
    }

    public void partialChat(AiChatDto dto, String userId) {
        final boolean[] isStart = {false};
        StringBuilder answerBuilder = new StringBuilder();
        TokenStream stream = agent.chat(userId, dto.getMessage());

        stream.onPartialResponse(text -> {
                    if (!isStart[0]) {
                        aiStreamingSupport.handlePartial(AiConstants.DEFAULT_RES_START_STREAM, userId);
                        isStart[0] = true;
                    }
                    if (text != null && !text.isEmpty()) {
                        answerBuilder.append(text);
                        aiStreamingSupport.handlePartial(text, userId);
                    }
                }
        ).onCompleteResponse(response -> {
            // 兜底：如果流式过程中没有任何片段，这里补发一次完整内容，保证前端能看到回复
            if (answerBuilder.isEmpty()) {
                String fullText = response.aiMessage().text();
                if (!fullText.isEmpty()) {
                    answerBuilder.append(fullText);
                    aiStreamingSupport.handlePartial(fullText, userId);
                }
            }

            aiStreamingSupport.handlePartial(AiConstants.DEFAULT_END_STREAM, userId);
            log.info("【AI】回复完成,用户id:{}", userId);

            // 持久化 AI 回复消息
            buildMsg(answerBuilder.toString(), userId);
        }).onError(err->{
            // 发生错误时，移除最后一条用户消息，避免上下文错乱
            aiStreamingSupport.handlePartial(AiConstants.DEFAULT_ERROR_START_STREAM, userId);
            aiStreamingSupport.handleErr(err);
        }).start();

    }

    private void buildMsg(String msg, String userId) {
        log.info("【AI】开始持久化回复内容，userId:{}", userId);
        PrivateMessageVO privateMessageVO = new PrivateMessageVO();
        privateMessageVO.setMsgId(GlobalIDWorker.generateId());
        privateMessageVO.setMsg(msg);
        privateMessageVO.setSendTime(new Timestamp(System.currentTimeMillis()));
        privateMessageVO.setType(MsgType.AI.getType());
        privateMessageVO.setReceiveId(userId);
        privateMessageVO.setEmitterId(AiConstants.AI_ID);
        privateMessageVO.setStatus(MsgEnum.READ.getStatus());
        privateMessageVO.setEmitterNick(AiConstants.AI_NICK);
        privateMessageVO.setSessionSeq(msgQueueUtil.getSessionSeqFromCache(MsgQueueUtil.getSessionId(userId, AiConstants.AI_ID)));
        msgMapper.save(
                privateMessageVO
        );
        log.info("【AI】回复内容已经持久化完成，userId:{}", userId);
    }
}
