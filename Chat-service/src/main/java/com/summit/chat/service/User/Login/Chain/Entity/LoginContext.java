package com.summit.chat.service.User.Login.Chain.Entity;

import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Dto.NormalLoginRequest;
import com.summit.chat.Dto.VerifyCodeLoginRequest;
import com.summit.chat.Exception.ChainConfigException;
import com.summit.chat.model.entity.mysql.User;
import com.summit.chat.model.vo.TokenVO;
import com.summit.chat.model.vo.UserVO;
import com.summit.chat.service.User.Login.Chain.LoginHandleChain;
import lombok.Data;

@Data
public class LoginContext  {
    private NormalLoginRequest request;
    private VerifyCodeLoginRequest codeLoginRequest;
    private LoginMode loginMode;
    private User user;
    private TokenVO tokenVO;
    private UserVO userVO;

    public void validate(LoginHandleChain node){
        if(node==null){
            throw new ChainConfigException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }
    }

    public boolean isPwForLoginMode(){
        return loginMode == LoginMode.PW;
    }
}

