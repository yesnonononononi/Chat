package com.summit.chat.service.message.GroupMessageResendCenter;

import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import com.summit.chat.model.vo.GroupMessageVO;
import com.summit.chat.service.Impl.Support.group.GroupMemberSupport.GroupMemberServiceCacheSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class GroupMsgResendSupport {

    @Autowired
    GroupMemberServiceCacheSupport groupMemberServiceCacheSupport;

    public void conduct(GroupMsgContext context){
        HashSet<SocketIOClient> clients = getClients(context);
        context.setClients(clients);
    }
    private HashSet<SocketIOClient> getClients(GroupMsgContext context) {
        GroupMessageVO msg = context.getMsg();
        ClientManager clientManager = context.getClientManager();
        Long groupId = msg.getGroupId();
        Long emitterId = msg.getEmitterId();
        HashSet<SocketIOClient> clients = new HashSet<>();

        // 获取所有成员的id
        List<String> memberIds = groupMemberServiceCacheSupport.getGroupMemberIds(groupId);

        // 获得所有成员的SocketIOClient
        if (memberIds != null) {
            for (String userId : memberIds) {
                //跳过发送者
                if (userId.equals(emitterId.toString())) continue;
                SocketIOClient client = clientManager.getClient(userId);
                // 离线TODO
                if (client == null) continue;
                clients.add(client);
            }
        }
        return clients;
    }
}
