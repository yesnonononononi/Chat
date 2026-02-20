package com.summit.chat.service.User.register.Chain.node;

import cn.hutool.core.util.ObjectUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.NormalRegisterLoginRequest;
import com.summit.chat.Mapper.UserMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.User;
import com.summit.chat.service.User.register.Chain.Entity.RegisterChainContext;
import com.summit.chat.service.User.register.Chain.Entity.RegisterHandleChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class UserExistValidateNode implements RegisterHandleChain {
    UserMapper userMapper;

    public UserExistValidateNode(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    RegisterHandleChain next;

    @Override
    public Result handle(RegisterChainContext chainContext) throws Exception {
        try {
            chainContext.validate(next);
            NormalRegisterLoginRequest request = chainContext.getRequest();
            //数据库校验
            User user = userMapper.getUserByPhone(request.getMobile());
            if(ObjectUtil.isNotNull(user))return Result.fail(UserConstants.USER_EXIST);
            return next.handle(chainContext);
        } catch (Exception e) {
            log.error("【用户注册】验证用户是否存在时出现异常: {}", e.getMessage(), e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
    }

    @Override
    public void setNext(RegisterHandleChain next) {
        this.next = next;
    }

}

