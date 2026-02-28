package com.summit.chat.service.message.GroupMessageResendCenter;

import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.GroupMsgConstants;
import com.summit.chat.Constants.MsgConstants;
import com.summit.chat.Exception.ChainConfigException;
import com.summit.chat.model.vo.GroupMessageVO;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import com.summit.chat.service.Impl.Support.group.GroupMessageSupport.GroupMessageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;


@Component
public class GroupMsgValidator extends GlobalValidatorImpl<GroupMessageVO> {

    @Autowired
    private GroupMessageValidator groupMessageValidator;
    public void conduct(GroupMsgContext context){
        if(context == null){
            throw new ChainConfigException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }
        if(context.getClientManager() == null)super.throwException(GroupMsgConstants.CLIENT_MANAGER_ERROR);
        validate(context.getMsg());
    }
    @Override
    public boolean validate(GroupMessageVO dto) {
        String msg = dto.getMsg();
        Timestamp createTime = dto.getCreateTime();
        if(dto.getGroupId() == null)super.throwException(GroupMsgConstants.GROUP_ID_ERROR);
        if(dto.getEmitterId()==null)super.throwException(GroupMsgConstants.EMITTER_ID_ERROR);
        if(createTime == null||createTime.before(new Timestamp(0)))super.throwException(GroupMsgConstants.CREATE_TIME_ERROR);
        if(msg == null || msg.isEmpty())super.throwException(GroupMsgConstants.MSG_ERROR_MIN);
        if(msg.length()>MsgConstants.MAX_MSG)super.throwException(GroupMsgConstants.MSG_ERROR_MAX);

        //查询群聊是否存在
        groupMessageValidator.isGroupExist(dto.getGroupId());
        //查询是否是群成员且未被拉黑,删除,禁言
        groupMessageValidator.checkUser(dto);

        return true;
    }
}
