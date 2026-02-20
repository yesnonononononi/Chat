package com.summit.chat.service.Impl.Support.group.GroupMessageSupport;

import cn.hutool.core.bean.BeanUtil;
import com.summit.chat.Enum.MsgEnum;
import com.summit.chat.Enum.MsgType;
import com.summit.chat.Utils.GlobalIDWorker;
import com.summit.chat.model.entity.GroupMessages;
import com.summit.chat.model.vo.GroupMessageVO;
import org.springframework.stereotype.Component;

@Component
public class GroupMessageSupport {
    public GroupMessages fillProp(GroupMessageVO vo){
        String messageType = vo.getMessageType();
        GroupMessages groupMessages = new GroupMessages();
        groupMessages.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
        
        // 先复制属性
        BeanUtil.copyProperties(vo,groupMessages);
        
        // 确保消息ID始终有效：优先使用前端传入的ID，如果无效则生成新的ID
        if(vo.getMsgId() == null || vo.getMsgId() == 0) {
    
            groupMessages.setId(generateMsgId());
        } else {
            // 如果msgId有效，则使用它
            groupMessages.setId(vo.getMsgId());
        }
        groupMessages.setMessageType(messageType == null ? MsgType.TEXT.getType() : messageType);
        groupMessages.setEmitterId(vo.getEmitterId());
        return groupMessages;
    }

    private long generateMsgId(){
        String s = GlobalIDWorker.generateId();
        return Long.parseLong(s);
    }
}
