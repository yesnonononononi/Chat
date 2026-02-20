package com.summit.chat.service.validate;

import cn.hutool.core.util.StrUtil;
import com.summit.chat.Constants.MsgConstants;
import com.summit.chat.Dto.ChatForPrivatePage;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.vo.PrivateMessageVO;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MsgValidator extends GlobalValidatorImpl<PrivateMessageVO> {
    @Override
    public boolean validate(PrivateMessageVO dto) {
        String userID = UserHolder.getUserID();
        baseValidate(dto,userID);
        if(dto.getMsgId() == null){
           super.throwException(MsgConstants.ARGV_ILLEGAL);
        }
        if(dto.getSendTime() == null){
           super.throwException(MsgConstants.TIME_NULL);
        }
        if(dto.getSendTime().compareTo(System.currentTimeMillis()-MsgConstants.MAX_WITHDRAWN_TIME)<0){
           super.throwException(MsgConstants.EXCEED_MAXTIME);
        }
        if(dto.getSendTime() > System.currentTimeMillis()){
            super.throwException(MsgConstants.SEND_TIME_ILLEGAL); // 新增常量："发送时间不能为未来时间"
        }
        return true;
    }

    public boolean pageValidate(ChatForPrivatePage dto){
        String userID = UserHolder.getUserID();
        if(dto==null||dto.getDto() == null) super.throwException(MsgConstants.ARGV_NULL);
        if(dto.getDto().getReceiveId() == null)super.throwException(MsgConstants.ARGV_NULL);
        baseValidate(dto.getDto(),userID);
        if (dto.getPage() == null || dto.getPageSize() ==null) {
            return false ;
        }
        return (dto.getPage() >= 0 && dto.getPageSize() > 0 && dto.getPageSize() < 100);

    }

    /**
     * 校验1,dto是否为null,2消息是否合法 3,发送者是否为自己
     * @param dto
     */
    public void baseValidate(PrivateMessageVO dto, String userId){
        //校验dto的id,msg
        if(dto == null){
           super.throwException(MsgConstants.ARGV_ILLEGAL);
        }
        if(StrUtil.isBlank(dto.getMsg())) {
           super.throwException(MsgConstants.MSG_NOT_EXIST);
        }
        if(dto.getMsg().length()>MsgConstants.MAX_MSG){
           super.throwException(MsgConstants.MSG_EXCEED_LENGTH);
        }
        if(!dto.getEmitterId().equals(userId)){
            super.throwException(MsgConstants.EXCEED_AUTH);
        }
    }
}

