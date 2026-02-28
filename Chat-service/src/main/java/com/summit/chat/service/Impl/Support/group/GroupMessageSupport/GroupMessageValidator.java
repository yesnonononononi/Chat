package com.summit.chat.service.Impl.Support.group.GroupMessageSupport;

import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Constants.GroupMessageConstants;
import com.summit.chat.Dto.GroupMessageDTO;
import com.summit.chat.Enum.GroupStatusEnum;
import com.summit.chat.Mapper.GroupMessageMapper;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.vo.GroupMembersVO;
import com.summit.chat.model.vo.GroupMessageVO;
import com.summit.chat.service.Impl.Support.group.AbstractGroupValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class GroupMessageValidator extends AbstractGroupValidator<GroupMessageDTO> {
    @Autowired
    private GroupMessageMapper groupMessageMapper;
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
        String emitterId = String.valueOf(vo.getEmitterId());
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
        GroupMembersVO member = getMember(vo.getGroupId(), userID);
        if(member == null){
            super.throwException(GroupMessageConstants.USER_NOT_MEMBER);
        }
        String userId = member.getUserId();
        if (userID == null) super.throwException(GroupConstants.USER_ID_ERROR);
        if (!userID.toString().equals(userId)) {
            super.throwException(GroupMessageConstants.USER_NOT_MEMBER);
        }
        Integer status = member.getStatus();
        if (status.equals(GroupStatusEnum.FORBIDDEN.getStatus())) {
            super.throwException(GroupMessageConstants.USER_FORBIDDEN);
        }
        vo.setNickName(member.getNickName());
        vo.setIcon(member.getAvatar());
    }

    public void checkMsgTime(GroupMessageVO vo) {
        if(vo.getMsgId() == null) {
            super.throwException(GroupConstants.GROUP_ID_ERROR);
        }

        GroupMessageVO msg = groupMessageMapper.queryMsgById(vo.getMsgId());
        if(msg == null) {
            super.throwException(GroupMessageConstants.NOT_OWNER);
        }

        String userId = UserHolder.getUserID();
        if(!String.valueOf(msg.getEmitterId()).equals(userId)) {
             super.throwException(GroupMessageConstants.NOT_OWNER);
        }

        Timestamp createTime = msg.getCreateTime();
        if (createTime == null || LocalDateTime.now().minusMinutes(GroupMessageConstants.MAX_WITHDRAWN_TIME).isAfter(createTime.toLocalDateTime()))
            super.throwException(GroupMessageConstants.MSG_TIME_ERROR);

        // 回填关键信息
        vo.setCreateTime(createTime);
        vo.setEmitterId(msg.getEmitterId());
        vo.setGroupId(msg.getGroupId());
        vo.setIsDeleted(0);
    }
}
