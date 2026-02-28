package com.summit.chat.service.User.register.Chain.node;

import cn.hutool.core.util.ObjectUtil;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.NormalRegisterLoginRequest;
import com.summit.chat.Result.Result;
import com.summit.chat.service.Impl.Support.User.UserValidator;
import com.summit.chat.service.User.register.Chain.Entity.RegisterChainContext;
import com.summit.chat.service.User.register.Chain.Entity.RegisterHandleChain;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Slf4j
@Component

public class NullValidateNode implements RegisterHandleChain {
    RegisterHandleChain next;
    @Autowired
    private UserValidator userValidator;

    @Override
    public Result handle(RegisterChainContext chainContext) throws Exception {
        chainContext.validate(next);
        if(ObjectUtil.isNull(chainContext)){
            return Result.fail(UserConstants.DATA_NULL);
        }
        // 新增：校验手机号/验证码非空
        NormalRegisterLoginRequest request = chainContext.getRequest();
        if (StringUtils.isBlank(request.getMobile())) {
            log.error("【用户注册】手机号为空");
            throw new IllegalArgumentException(UserConstants.DATA_NULL);
        }
        if (StringUtils.isBlank(request.getVerifyCode())) {
            log.error("【用户注册】验证码为空");
            throw new IllegalArgumentException(UserConstants.DATA_NULL);
        }

        return next.handle(chainContext);
    }

    @Override
    public void setNext(RegisterHandleChain next) {
        this.next =next;
    }
}
