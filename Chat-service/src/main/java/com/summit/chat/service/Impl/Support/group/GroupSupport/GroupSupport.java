package com.summit.chat.service.Impl.Support.group.GroupSupport;

import cn.hutool.core.bean.BeanUtil;
import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Dto.GroupChatDto;
import com.summit.chat.Enum.GroupStatusEnum;
import com.summit.chat.Utils.GlobalIDWorker;
import com.summit.chat.model.entity.mysql.GroupChat;
import org.springframework.stereotype.Component;

@Component
public class GroupSupport {

    public String generateGroupId() {
        return GlobalIDWorker.generateId();
    }

    public GroupChat fillProp(GroupChatDto dto){
        GroupChat groupChat = new GroupChat();
        BeanUtil.copyProperties(dto,groupChat);
        groupChat.setId(Long.valueOf(generateGroupId()));
        if(dto.getGroupDescription() == null){
            groupChat.setGroupDescription(GroupConstants.DEFAULT_DESCRIPTION);
        }
        groupChat.setStatus(GroupStatusEnum.NORMAL.getStatus());
        groupChat.setNumber(1);
        return groupChat;
    }


}
