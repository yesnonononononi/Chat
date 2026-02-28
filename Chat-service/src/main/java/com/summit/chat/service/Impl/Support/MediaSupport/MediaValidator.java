package com.summit.chat.service.Impl.Support.MediaSupport;


import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Constants.UserLinkConstants;
import com.summit.chat.Enum.UserCallStateEnum;
import com.summit.chat.Mapper.MediaMapper;
import com.summit.chat.Mapper.UserLinkMapper;
import com.summit.chat.Mapper.UserMapper;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MediaValidator extends GlobalValidatorImpl<String> {
    @Autowired
    private UserLinkMapper mapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MediaMapper mediaMapper;

    @Override
    public boolean validate(String dto) {
        return false;
    }

    public void isFriend(String userID, String receiverId) {
        Integer i = mapper.linkExist(userID, receiverId);
        if(  i != 0)return;
        super.throwException(UserLinkConstants.LINK_UN_EXIST);
    }

    public void validateState(String receiverId) {
        Integer state = userMapper.getUserState(receiverId);
        if(Objects.equals(state, UserCallStateEnum.IDLE.getState()))return;
        super.throwException(UserConstants.USER_IS_BUSY);
    }

    public void isExistApply(String emitterId, String receiverId) {
        Integer count = mediaMapper.countApply(emitterId, receiverId);
        if (count == null || count == 0) {
            super.throwException("申请不存在或已处理");
        }
    }
}
