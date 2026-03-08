package com.summit.chat.service.Impl.Support.group.GroupMessageSupport;

import cn.hutool.core.bean.BeanUtil;
import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Dto.MessageWithdrawnDTO;
import com.summit.chat.Enum.ChatEvent;
import com.summit.chat.Enum.MsgType;
import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import com.summit.chat.Mapper.Mysql.GroupMemberMapper;
import com.summit.chat.Utils.GlobalIDWorker;
import com.summit.chat.model.entity.mysql.GroupMessages;
import com.summit.chat.model.vo.GroupMembersVO;
import com.summit.chat.model.vo.GroupMessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupMessageSupport {
    @Autowired
    private ClientManager clientManager;
    @Autowired
    private GroupMemberMapper groupMemberMapper;
    public GroupMessages fillProp(GroupMessageVO vo){
        String messageType = vo.getType();
        GroupMessages groupMessages = new GroupMessages();
        groupMessages.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
        
        // 先复制属性
        BeanUtil.copyProperties(vo,groupMessages);
        
        // 确保消息ID始终有效：优先使用前端传入的ID，如果无效则生成新的ID
        if(vo.getMsgId() == null || vo.getMsgId().isEmpty()) {
    
            groupMessages.setId(generateMsgId());
        } else {
            // 如果msgId有效，则使用它
            groupMessages.setId(vo.getMsgId());
        }
        groupMessages.setType(messageType == null ? MsgType.TEXT.getType() : messageType);
        groupMessages.setEmitterId(vo.getEmitterId());
        return groupMessages;
    }

    private String generateMsgId(){
        return GlobalIDWorker.generateId();
    }


    public void withdrawn(GroupMessageVO vo) {
        String msgId = vo.getMsgId();
        Long emitterId = vo.getEmitterId();
        Long groupId = vo.getGroupId();
        List<GroupMembersVO> groupMembers = getGroupMembers(groupId);
        MessageWithdrawnDTO group = MessageWithdrawnDTO.builder()
                .msgId(String.valueOf(msgId))
                .emitterId(String.valueOf(emitterId))
                .type("group")
                .groupId(String.valueOf(groupId))
                .build();
        sendAll(groupMembers,group);
    }




    private List<GroupMembersVO> getGroupMembers(Long groupId) {
        return groupMemberMapper.queryMemberByGroupId(groupId);
    }
    private void sendAll(List<GroupMembersVO> vo,MessageWithdrawnDTO dto){
        vo.forEach(v->{
            String userId = v.getUserId();
            SocketIOClient client = clientManager.getClient(userId);
            if(client != null ){
                client.sendEvent(ChatEvent.CHAT_WITHDRAWN.getType(), dto);
            }
        });
    }
}
