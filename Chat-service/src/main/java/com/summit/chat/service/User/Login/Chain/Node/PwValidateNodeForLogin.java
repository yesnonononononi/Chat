package com.summit.chat.service.User.Login.Chain.Node;

import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.NormalLoginRequest;
import com.summit.chat.Exception.ChainConfigException;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.EncryptUtil;
import com.summit.chat.Utils.RSAUtil;
import com.summit.chat.model.entity.User;
import com.summit.chat.service.User.Login.Chain.Entity.LoginContext;
import com.summit.chat.service.User.Login.Chain.LoginHandleChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Scope("prototype")
@Component
public class PwValidateNodeForLogin implements LoginHandleChain {

    LoginHandleChain next;

    @Value("${rsa.private-key}")
    private String privateKey;
    @Override
    public Result handle(LoginContext context) throws Exception {
        NormalLoginRequest request = context.getRequest();
        User user = context.getUser();
        if(request == null || user == null){
            throw new ChainConfigException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }
        String pw = request.getPw();
        //解密
        String decryptedPw = RSAUtil.decrypt(pw, privateKey);
        if (EncryptUtil.identity(decryptedPw,user.getPw())) {
            return next.handle(context);
        }
        return Result.fail(UserConstants.PASSWORD_UNRIGHT);
    }

    @Override
    public void setNext(LoginHandleChain next) {
        this.next = next;
    }
}

