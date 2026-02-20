package com.summit.chat.service.Impl.Support.group.GroupMemberSupport;

import cn.hutool.core.bean.BeanUtil;
import com.summit.chat.Dto.GroupMemberDTO;
import com.summit.chat.Enum.GroupRole;
import com.summit.chat.model.entity.GroupMembers;
import org.springframework.stereotype.Component;

@Component
public class GroupMemberSupport {
    public GroupMembers fillProps(GroupMemberDTO dto){
        GroupMembers groupMembers = new GroupMembers();
        BeanUtil.copyProperties(dto,groupMembers);
        groupMembers.setRole(GroupRole.MEMBER.getRole());
        return groupMembers;
    }
}
