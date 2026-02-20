package com.summit.chat.service.Impl.Support.group.GroupMessageSupport;

import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Constants.GroupMessageConstants;
import com.summit.chat.Dto.GroupMessageDTO;
import com.summit.chat.Enum.GroupStatusEnum;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.vo.GroupMembersVO;
import com.summit.chat.model.vo.GroupMessageVO;
import com.summit.chat.service.Impl.Support.group.AbstractGroupValidator;
import org.springframework.stereotype.Component;

@Component
public class GroupMessageValidator extends AbstractGroupValidator<GroupMessageDTO> {
    @Override
    public boolean validate(GroupMessageDTO dto) {
        Long groupId = dto.getGroupId();
        String senderId = dto.getEmitterId();
        if (groupId == null) {
            super.throwException(GroupConstants.GROUP_ID_ERROR);
        }
        if (senderId == null) {
            super.throwException(GroupConstants.USER_ID_ERROR);
        }
        return false;
    }

    /**
     * 校验群消息id
     *
     * @param vo
     */
    public void checkId(GroupMessageVO vo) {
        String userID = UserHolder.getUserID();
        Long emitterId = vo.getEmitterId();
        if (emitterId == null) {
            super.throwException(GroupConstants.USER_ID_ERROR);
        }
        if (!emitterId.equals(userID)) {
            super.throwException(GroupMessageConstants.NOT_OWNER);
        }
    }


    /**
     * 检查发送消息的是否是群成员
     * @param vo
     */
    public void checkUser(GroupMessageVO vo) {
        Long userID = vo.getEmitterId();
        GroupMembersVO member = getMember(vo.getGroupId(), vo.getEmitterId());
        String userId = member.getUserId();
        if (userID == null) super.throwException(GroupConstants.USER_ID_ERROR);
        if (!userID.toString().equals(userId)) {
            super.throwException(GroupMessageConstants.USER_NOT_MEMBER);
        }
        Integer status = member.getStatus();
        if (status.equals(GroupStatusEnum.BLACK.getStatus())) {
            super.throwException(GroupMessageConstants.USER_BLACK);
        }
        if (status.equals(GroupStatusEnum.FORBIDDEN.getStatus())) {
            super.throwException(GroupMessageConstants.USER_FORBIDDEN);
        }
        vo.setNickName(member.getNickName());
        vo.setIcon(member.getAvatar());
    }
}
