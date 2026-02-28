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
        if (msg.getMsgId() == null || msg.getMsgId().isEmpty()) {
             String s = generateMsgId();
             msg.setMsgId(s);
        }
        
        String messageType = msg.getType();
        //没有传入消息类型，默认为文本
        if(messageType == null){
            msg.setType(MsgType.TEXT.getType());
        }
        if(msg.getCreateTime() == null){
            msg.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
        }
    }
    private String generateMsgId(){
        return GlobalIDWorker.generateId();
    }
}
