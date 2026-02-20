package com.summit.chat.service.message.GroupMessageResendCenter;

import com.summit.chat.Enum.MsgType;
import com.summit.chat.Utils.GlobalIDWorker;
import com.summit.chat.model.vo.GroupMessageVO;
import org.springframework.stereotype.Component;

/**
 * 消息的处理中心
 */
@Component
public class GroupMsgWorker {
    public void conduct(GroupMsgContext context){
        GroupMessageVO msg = context.getMsg();
        // 如果消息ID为空，则生成新的ID (兼容逻辑)
        if (msg.getMsgId() == null || msg.getMsgId() == 0) {
             String s = generateMsgId();
             msg.setMsgId(Long.valueOf(s));
        }
        
        String messageType = msg.getMessageType();
        //没有传入消息类型，默认为文本
        if(messageType == null){
            msg.setMessageType(MsgType.TEXT.getType());
        }
    }
    private String generateMsgId(){
        return GlobalIDWorker.generateId();
    }
}
