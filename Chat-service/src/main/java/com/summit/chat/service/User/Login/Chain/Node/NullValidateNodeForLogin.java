package com.summit.chat.service.User.Login.Chain.Node;

import cn.hutool.core.util.ObjectUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Result.Result;
import com.summit.chat.service.User.Login.Chain.Entity.LoginContext;
import com.summit.chat.service.User.Login.Chain.Entity.LoginMode;
import com.summit.chat.service.User.Login.Chain.LoginHandleChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Slf4j
@Scope("prototype")
@Component("NullValidateForLogin")
public class NullValidateNodeForLogin implements LoginHandleChain {
    LoginHandleChain next;


    @Override
    public Result handle(LoginContext context) throws Exception {
        if(context == null){
            log.error(BaseConstants.CHAIN_CONFIG_EXCEPTION);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
        context.validate(next);
        LoginMode loginMode = context.getLoginMode();
        Object request = null;
        if(LoginMode.PW == loginMode){
             request = context.getRequest();
        }
        else if(LoginMode.VERIFYCODE == loginMode){
            request =context.getCodeLoginRequest();
        }

        //判断请求是否为空值
        if( ObjectUtil.isNull(request)||ObjectUtil.isNull(context.getLoginMode())){
            return Result.fail(UserConstants.ILLEGAL_CHAR);
        }
        return next.handle(context);
    }

    @Override
    public void setNext(LoginHandleChain next) {
        this.next = next;
    }
}
