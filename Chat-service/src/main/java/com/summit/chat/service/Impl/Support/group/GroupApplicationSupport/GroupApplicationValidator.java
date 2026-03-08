package com.summit.chat.service.Impl.Support.group.GroupApplicationSupport;

import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Dto.GroupApplicationDTO;
import com.summit.chat.Mapper.Mysql.GroupApplicationMapper;
import com.summit.chat.model.vo.GroupMembersVO;
import com.summit.chat.service.Impl.Support.group.AbstractGroupValidator;
import com.summit.chat.service.Impl.Support.group.GroupMemberSupport.GroupMemberServiceCacheSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupApplicationValidator extends AbstractGroupValidator<GroupApplicationDTO> {

    @Autowired
    private GroupApplicationMapper groupApplicationMapper;

    @Autowired
    private GroupMemberServiceCacheSupport groupMemberServiceCacheSupport;

    @Override
    public boolean validate(GroupApplicationDTO dto) {
        if (dto == null) {
            super.throwException(GroupConstants.GROUP_ID_ERROR);
        }
        return true;
    }

    /**
     * 校验申请必须包含基本信息
     */
    public GroupApplicationValidator validateApply(GroupApplicationDTO dto) {
        if (dto.getGroupId() == null) super.throwException(GroupConstants.GROUP_ID_ERROR);
        if (dto.getApplicantId() == null) super.throwException(GroupConstants.USER_ID_ERROR);
        return this;
    }

    /**
     * 校验用户是否已经是群成员
     */
    public GroupApplicationValidator isAlreadyMember(Long groupId, Long userId) {
        List<String> memberIds = groupMemberServiceCacheSupport.getGroupMemberIds(groupId);
        if (memberIds != null && memberIds.contains(String.valueOf(userId))) {
             super.throwException("该用户已是群成员");
        }
        return this;
    }

    /**
     * 校验是否已有待处理的申请
     */
    public GroupApplicationValidator hasPendingApplication(Long groupId, Long applicantId) {
        Integer count = groupApplicationMapper.countPendingApplication(groupId, applicantId);
        if (count != null && count > 0) {
            throw new RuntimeException("已存在待处理的申请"); 
        }
        return this;
    }

    /**
     * 校验操作人是否有权限（群主或管理员）
     */
    public GroupApplicationValidator hasPermission(Long groupId, Long operatorId) {
        GroupMembersVO operator = getMember(groupId, operatorId);
        super.verifyOwnerOrAdmin(operator);
        return this;
    }
}
