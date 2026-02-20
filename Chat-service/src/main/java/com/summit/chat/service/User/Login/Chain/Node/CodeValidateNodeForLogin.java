package com.summit.chat.service.User.Login.Chain.Node;

import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.VerifyCodeLoginRequest;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.AliyunUtil;
import com.summit.chat.service.User.Login.Chain.Entity.LoginContext;
import com.summit.chat.service.User.Login.Chain.LoginHandleChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Scope("prototype")
@Component("CodealidateNodeForLogin")
@Slf4j
public class CodeValidateNodeForLogin implements LoginHandleChain {
    LoginHandleChain next;
    AliyunUtil aliyunUtil;

    public CodeValidateNodeForLogin(AliyunUtil aliyunUtil) {
        this.aliyunUtil = aliyunUtil;
    }

    @Override
    public Result handle(LoginContext context){
        context.validate(next);
        VerifyCodeLoginRequest request = context.getCodeLoginRequest();
        //验证码校验
        try {
            if (!aliyunUtil.validate(request.getMobile(), request.getVerifyCode())) {
                return Result.fail(UserConstants.CODE_ERROR);
            }
            return next.handle(context);
        } catch (Exception e) {
            log.error("【用户登录】验证码校验出现问题:{}",request.getMobile() ,e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
    }

    @Override
    public void setNext(LoginHandleChain next) {
        this.next = next;
    }
}
