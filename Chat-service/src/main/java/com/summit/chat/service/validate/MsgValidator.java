package com.summit.chat.service.validate;

import cn.hutool.core.util.StrUtil;
import com.summit.chat.Constants.MsgConstants;
import com.summit.chat.Dto.ChatForPrivatePage;
import com.summit.chat.Mapper.MsgMapper;
import com.summit.chat.Mapper.UserLinkMapper;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.vo.PrivateMessageVO;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@Slf4j
public class MsgValidator extends GlobalValidatorImpl<PrivateMessageVO> {
    @Autowired
    private UserLinkMapper userLinkMapper;
    @Autowired
    private MsgMapper mapper;

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

        if(dto.getSendTime().getTime() > System.currentTimeMillis()){
            super.throwException(MsgConstants.SEND_TIME_ILLEGAL); // 新增常量："发送时间不能为未来时间"
        }
        return true;
    }

    public boolean pageValidate(ChatForPrivatePage dto){
        String userID = UserHolder.getUserID();
        if(dto==null||dto.getDto() == null) super.throwException(MsgConstants.ARGV_NULL);
        if(dto.getDto().getReceiveId() == null)super.throwException(MsgConstants.ARGV_NULL);
        
        // 校验好友关系 (查询历史消息也需要好友关系)
        Integer isFriend = userLinkMapper.linkExist(userID, dto.getDto().getReceiveId());
        if (isFriend == null || isFriend == 0) {
            // 如果不是好友，也允许自己看自己的 
            if(!userID.equals(dto.getDto().getReceiveId())) {
                 super.throwException(MsgConstants.NOT_FRIEND);
            }
        }

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

    public void validateSend(PrivateMessageVO dto, String userId){
        baseValidate(dto,userId);
        if(StrUtil.isBlank(dto.getReceiveId())){
            super.throwException(MsgConstants.ARGV_ILLEGAL);
        }
        //如果是自己给自己发消息,则不需要校验好友关系
        if(dto.getEmitterId().equals(dto.getReceiveId())){
            return;
        }
        // 校验好友关系
        Integer isFriend = userLinkMapper.linkExist(dto.getEmitterId(), dto.getReceiveId());
        if (isFriend == null || isFriend == 0) {
            super.throwException(MsgConstants.NOT_FRIEND);
        }
    }

    public void checkMsgTime(PrivateMessageVO dto) {
        if(StrUtil.isBlank(dto.getMsgId())) {
            super.throwException(MsgConstants.ARGV_ILLEGAL);
        }

        // 1. 查询特定消息
        PrivateMessageVO msg = mapper.queryById(dto.getMsgId());
        if(msg == null) {
            super.throwException(MsgConstants.MSG_NOT_EXIST);
        }


        // 2. 校验权限 (是否为发送者)
        String userId = UserHolder.getUserID();
        if(!msg.getEmitterId().equals(userId)) {
             super.throwException(MsgConstants.EXCEED_AUTH);
        }

        // 3. 校验时间
        Timestamp sendTime = msg.getSendTime();
        if(sendTime == null || LocalDateTime.now().minusMinutes(MsgConstants.MAX_WITHDRAWN_TIME).isAfter(sendTime.toLocalDateTime())){
            super.throwException(MsgConstants.EXCEED_MAX_WITHDRAWN_TIME);
        }
        
        // 4. 回填关键字段到 dto，供后续 update 使用
        dto.setSendTime(sendTime);
        dto.setEmitterId(msg.getEmitterId());
        dto.setReceiveId(msg.getReceiveId());
    }
}

