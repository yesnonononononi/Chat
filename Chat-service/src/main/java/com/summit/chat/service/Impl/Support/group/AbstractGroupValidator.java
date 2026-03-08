package com.summit.chat.service.Impl.Support.group;

import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Constants.GroupMsgConstants;
import com.summit.chat.Enum.GroupRole;
import com.summit.chat.Enum.GroupStateEnum;
import com.summit.chat.Mapper.Mysql.GroupMapper;
import com.summit.chat.Mapper.Mysql.GroupMemberMapper;
import com.summit.chat.model.vo.GroupChatVO;
import com.summit.chat.model.vo.GroupMembersVO;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public abstract class AbstractGroupValidator<T> extends GlobalValidatorImpl<T> {

    @Autowired
    protected GroupMapper groupMapper;

    @Autowired
    protected GroupMemberMapper groupMemberMapper;

    @Autowired
    protected com.summit.chat.service.Impl.Support.group.GroupSupport.GroupServiceCacheSupport groupServiceCacheSupport;

    /**
     * 判断群聊是否存在
     */
    public GroupChatVO isGroupExist(Long groupId) {
        if (groupId == null) super.throwException(GroupConstants.GROUP_ID_ERROR);
        // 使用缓存查询群聊信息
        GroupChatVO groupChatVO = groupServiceCacheSupport.getGroupInfo(groupId);
        if (groupChatVO == null) super.throwException(GroupConstants.GROUP_NOT_EXIST);
        if(Objects.equals(groupChatVO.getStatus(), GroupStateEnum.BANNED.getState()))super.throwException(GroupConstants.GROUP_BLACK);
        return groupChatVO;
    }

    /**
     * 获取群信息
     */
    public GroupChatVO getGroupById(Long groupId) {
        return groupMapper.queryGroupById(groupId);
    }

    /**
     * 获取群成员信息 (根据群ID和用户ID)
     */
    public GroupMembersVO getMember(Long groupId, Long userId) {
        return groupMemberMapper.queryMemberByGroupIdAndUserId(groupId, userId);
    }

    /**
     * 获取群成员信息 (根据主键ID)
     */
    public GroupMembersVO getMemberById(String id) {
        return groupMemberMapper.queryGroupMemberById(id);
    }

    /**
     * 校验群成员是否已满
     */
    public void verifyGroupFull(GroupChatVO group) {
        if (group != null && group.getNumber() >= GroupConstants.GROUP_MAX_NUMBER)
            super.throwException(GroupConstants.GROUP_FULL);
    }

    /**
     * 校验是否是群主
     */
    public boolean verifyOwner(GroupMembersVO member) {
        return (member != null && GroupRole.OWNER.getRole().equals(member.getRole()));


    }

    /**
     * 校验是否是群主或管理员
     */
    public void verifyOwnerOrAdmin(GroupMembersVO member) {
        if (member == null) {
            super.throwException(GroupMsgConstants.NOT_OWNER);
        }
        String role = member.getRole();
        boolean isOwner = GroupRole.OWNER.getRole().equals(role) || GroupRole.ADMIN.getRole().equals(role);

        if (!isOwner ) {
             super.throwException(GroupMsgConstants.NOT_OWNER);
        }
    }
}
