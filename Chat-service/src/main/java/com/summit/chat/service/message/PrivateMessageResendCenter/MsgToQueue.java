package com.summit.chat.service.message.PrivateMessageResendCenter;

import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.ChatConstants;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Utils.MsgQueueUtil;
import com.summit.chat.model.vo.PrivateMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MsgToQueue {
    @Autowired
    MsgQueueUtil msgQueueUtil;

    public void conduct(MsgContext<PrivateMessageVO> context){
        try {
            PrivateMessageVO msg = context.getMsg();
            msgQueueUtil.sendMsgToExChange(msg, msg.getMsgId());
        }catch (Exception e){
            log.error("将用户{}的消息放入消息队列时出现问题",context.getEmitterClient().get(ChatConstants.USER_INFO),e);
            throw new BusinessException(BaseConstants.SERVER_EXCEPTION);
        }
    }
}

