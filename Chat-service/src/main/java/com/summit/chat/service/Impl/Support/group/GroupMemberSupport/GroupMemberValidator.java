package com.summit.chat.service.Impl.Support.group.GroupMemberSupport;

import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Constants.GroupMemberConstants;
import com.summit.chat.Dto.GroupMemberDTO;
import com.summit.chat.model.vo.GroupChatVO;
import com.summit.chat.model.vo.GroupMembersVO;
import com.summit.chat.service.Impl.Support.group.AbstractGroupValidator;
import org.springframework.stereotype.Component;

@Component
public class GroupMemberValidator extends AbstractGroupValidator<GroupMemberDTO> {

    @Override
    public boolean validate(GroupMemberDTO dto) {
        Long userId = dto.getUserId();
        Long memberId = dto.getMemberId();
        Long groupId = dto.getGroupId();
        if (userId == null) super.throwException(GroupConstants.USER_ID_ERROR);
        if (groupId == null) super.throwException(GroupConstants.GROUP_ID_ERROR);
        if (memberId == null) super.throwException(GroupConstants.MEMBER_ID_ERROR);
        return true;
    }

    //判断群成员是否是群主
    public GroupMemberValidator isOwner(GroupMembersVO memberInfo) {
        boolean b = super.verifyOwner(memberInfo);
        if(b){
            super.throwException(GroupMemberConstants.OWNER_ERROR);
        }
        return this;
    }

    //判断群成员是否已满
    public GroupMemberValidator isFull(GroupChatVO groupById) {
        super.verifyGroupFull(groupById);
        return this;
    }

    /**
     * 判断群成员是否存在
     */
    public GroupMembersVO isMemberExist(Long groupId,Long userID) {
        GroupMembersVO groupMembersVO = getMember(groupId,userID);
        if (groupMembersVO == null) super.throwException(GroupMemberConstants.MEMBER_NOT_EXIST);
        return groupMembersVO;
    }


}
