package com.summit.chat.service.Impl.Support.group.GroupApplicationSupport;

import com.summit.chat.Enum.GroupRole;
import com.summit.chat.Enum.GroupStatusEnum;
import com.summit.chat.Mapper.Mysql.GroupMemberMapper;
import com.summit.chat.model.entity.mysql.GroupApplications;
import com.summit.chat.model.entity.mysql.GroupMembers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GroupApplySupport {

    private final GroupMemberMapper groupMemberMapper;

    public GroupApplySupport(GroupMemberMapper groupMemberMapper) {
        this.groupMemberMapper = groupMemberMapper;
    }

    public void addMember(GroupApplications app){
        // 添加群成员
        GroupMembers members = new GroupMembers();
        members.setGroupId(app.getGroupId());
        members.setUserId(app.getApplicantId());
        members.setRole(GroupRole.MEMBER.getRole()); // 普通成员
        members.setStatus(GroupStatusEnum.NORMAL.getStatus());
        // status 默认可能在Mapper或DB处理，或者DTO里设置
        groupMemberMapper.addMember(members);
    }
}
