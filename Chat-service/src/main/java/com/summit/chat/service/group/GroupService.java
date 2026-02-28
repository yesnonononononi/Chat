package com.summit.chat.service.group;

import com.summit.chat.Dto.GroupChatDto;
import com.summit.chat.Result.Result;

public interface GroupService {
    Result addGroup(GroupChatDto dto);
    Result delGroup(Long groupId, String userId);
    Result putGroup(GroupChatDto dto);
    Result queryGroupById(Long groupId);
    Result queryUserGroupByUserId(String userId,Integer page,Integer pageSize);
    Result queryGroupByName(String groupName,Integer page,Integer pageSize);



    public Result queryGroupByUserId(String userId);
}
