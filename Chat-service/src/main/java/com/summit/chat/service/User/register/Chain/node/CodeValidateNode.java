package com.summit.chat.service.User.register.Chain.node;

import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.NormalRegisterLoginRequest;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.AliyunUtil;
import com.summit.chat.service.User.register.Chain.Entity.RegisterChainContext;
import com.summit.chat.service.User.register.Chain.Entity.RegisterHandleChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j

@Component
public class CodeValidateNode implements RegisterHandleChain {
    AliyunUtil aliyunUtil;

    public CodeValidateNode(AliyunUtil aliyunUtil) {
        this.aliyunUtil = aliyunUtil;
    }

    RegisterHandleChain next;

    @Override
    public Result handle(RegisterChainContext chainContext) throws Exception {

        chainContext.validate(next);

        NormalRegisterLoginRequest request = chainContext.getRequest();
            //校验验证码
            boolean validate = aliyunUtil.validate(request.getMobile(), request.getVerifyCode());
            if(!validate){
                log.error("【用户注册】验证码校验错误:手机号:{}",request.getMobile());
                return Result.fail(UserConstants.CODE_ERROR);
            }
            return next.handle(chainContext);



    }

    @Override
    public void setNext(RegisterHandleChain next) {
        this.next = next;
    }



}
