package com.summit.chat.service.message.PrivateMessageResendCenter;

import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.ChatConstants;
import com.summit.chat.Constants.MsgConstants;
import com.summit.chat.Mapper.Mysql.UserLinkMapper;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.vo.PrivateMessageVO;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MsgValidate extends GlobalValidatorImpl<MsgContext<PrivateMessageVO>> {
    @Autowired
    private UserLinkMapper userLinkMapper;

    public void conduct(MsgContext<PrivateMessageVO> context){
        validate(context);
    }
    public boolean validate(MsgContext<PrivateMessageVO> context){
        if(context==null || context.getEmitterClient() == null){
            super.throwException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }
        SocketIOClient emitterClient = context.getEmitterClient();

        PrivateMessageVO msg = context.getMsg();
        if(msg == null){
            super.throwException(MsgConstants.ARGV_ILLEGAL);
        }
        if(msg.getReceiveId() == null || msg.getEmitterId() == null ||msg.getMsg() == null){
            super.throwException(MsgConstants.ARGV_NULL);
        }
        if(msg.getMsg().length() > MsgConstants.MAX_MSG){
            super.throwException(MsgConstants.MSG_EXCEED_LENGTH);
        }


        try{
            String userID = emitterClient.get(ChatConstants.USER_INFO);
            if(userID == null){
                super.throwException(BaseConstants.UNCACHE_USERID);
            }
            if(!msg.getEmitterId().equals(userID)){
                super.throwException(MsgConstants.EXCEED_AUTH);
            }
            //如果是自己给自己发消息,则不需要校验好友关系
            if(msg.getEmitterId().equals(msg.getReceiveId())){
                return true;
            }
            // 校验好友关系
            Integer isFriend = userLinkMapper.linkExist(msg.getEmitterId(), msg.getReceiveId());
            if (isFriend == null || isFriend == 0) {
                super.throwException(MsgConstants.NOT_FRIEND);
            }
        }finally {
            UserHolder.remove();
        }
        return true;
    }
}

