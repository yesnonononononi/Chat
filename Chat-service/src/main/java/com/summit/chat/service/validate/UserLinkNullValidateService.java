package com.summit.chat.service.validate;

import cn.hutool.core.util.ObjectUtil;
import com.summit.chat.Constants.UserLinkConstants;
import com.summit.chat.Dto.UserLinkDto;
import com.summit.chat.Mapper.UserLinkMapper;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.vo.UserLinkVO;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserLinkNullValidateService extends GlobalValidatorImpl<UserLinkDto> {
    @Autowired
    UserLinkMapper userLinkMapper;
    public boolean baseValidate(Object o) {
     return ObjectUtil.isNull(o);
    }
    public List<UserLinkVO> filter(List<UserLinkVO> list){
        if(list.isEmpty()){
            return list;
        }
        return list.stream().filter(each -> each.getLinkId() != null).toList();
    }


    public void validateOfDelete(UserLinkDto dto) {
        String linkID = dto.getLinkID();
        String userID = dto.getUserID();
        String userID1 = UserHolder.getUserID();
        //判断是否是本人
        if(!userID1.equals(userID)){
            super.throwException(UserLinkConstants.ILLEGAL_REQUEST);
        }
        //是否存在这样一段关系
        Integer i = userLinkMapper.linkExist(userID, linkID);
        if(i == null || i == 0){
            super.throwException(UserLinkConstants.LINK_UN_EXIST);
        }


    }

    @Override
    public boolean validate(UserLinkDto dto) {
        return false;
    }
}


