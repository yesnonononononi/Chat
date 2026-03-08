package com.summit.chat.service.Impl.Support.group.GroupSupport;

import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Dto.GroupChatDto;
import com.summit.chat.Mapper.Mysql.GroupMapper;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.vo.GroupChatVO;
import com.summit.chat.model.vo.GroupMembersVO;
import com.summit.chat.service.Impl.Support.group.AbstractGroupValidator;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupValidator extends AbstractGroupValidator<GroupChatDto> {

    @Autowired
    private GroupMapper groupMapper;

    @Override
    public boolean validate(GroupChatDto dto) {
        Long creatorId = dto.getCreatorId();
        if(creatorId == null){
            super.throwException(GroupConstants.CREATOR_ID_NULL);
            return false;
        }
        return true;
    }

    public void checkName(String groupName){
        if(groupName == null || StringUtil.isBlank(groupName))super.throwException(GroupConstants.GROUP_NAME_NULL);
        if(groupName.length() > GroupConstants.DEFAULT_MAX_GROUP_NAME_LENGTH)super.throwException(GroupConstants.GROUP_NAME_TOO_LONG);
    }


    /**
     * 检测用户id是否合法
     * @param operatorId
     */
    public void checkId(Long operatorId){
        String userID = UserHolder.getUserID();
        if(!String.valueOf(operatorId).equals(userID))super.throwException(GroupConstants.NOT_OWNER);
    }

    /**
     * 校验群聊创建数量限制
     * @param creatorId 创建者ID
     */
    public void verifyGroupLimit(Long creatorId) {
        String userId = String.valueOf(creatorId);
        List<GroupChatVO> userGroups = groupMapper.queryUserGroupByUserId(userId);
        int currentCount = userGroups.size();
        
        int maxGroups = GroupConstants.MAX_GROUPS_NORMAL;

        if (currentCount >= maxGroups) {
            super.throwException(String.format(GroupConstants.GROUP_LIMIT_EXCEEDED,
                    GroupConstants.MAX_GROUPS_NORMAL));
        }
    }

    /**
     * 修改群聊信息时检测
     */
    public void putGroupValidate(Long groupId,String userId){
        //判断群聊id是否为空
        if(groupId == null)super.throwException(GroupConstants.GROUP_ID_ERROR);
        //判断用户id是否匹配
        checkId(Long.valueOf(userId));
        //判断群聊是否存在
        isGroupExist(groupId);
        //获取用户信息
        GroupMembersVO member =getMember(groupId, Long.valueOf(userId));
        //判断用户是否是群聊的创建者
        verifyOwnerOrAdmin(member);
    }

    public void checkDescription(String groupDescription) {
        if(groupDescription.length() >= GroupConstants.DEFAULT_MAX_GROUP_DESCRIPTION)super.throwException(GroupConstants.GROUP_DESCRIPTION_TOO_LONG);
    }
}
