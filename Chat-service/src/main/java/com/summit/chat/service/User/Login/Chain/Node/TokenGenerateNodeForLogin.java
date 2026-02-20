package com.summit.chat.service.User.Login.Chain.Node;

import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Result.Result;
import com.summit.chat.model.vo.TokenVO;
import com.summit.chat.service.Impl.Support.TokenSupport;
import com.summit.chat.service.User.Login.Chain.Entity.LoginContext;
import com.summit.chat.service.User.Login.Chain.LoginHandleChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component("TokenGenerateForLogin")
@Scope("prototype")
@Slf4j
public class TokenGenerateNodeForLogin implements LoginHandleChain {
    public LoginHandleChain next;
    TokenSupport tokenSupport;

    public TokenGenerateNodeForLogin(TokenSupport tokenSupport) {
        this.tokenSupport = tokenSupport;
    }

    @Override
    public Result handle(LoginContext context) throws Exception {
        context.validate(next);

        String token = tokenSupport.builder()
                .generate(UserConstants.CACHE_USER_TOKEN)
                .expire(UserConstants.TOKEN_TIMEOUT, TimeUnit.MINUTES)
                .build();

        TokenVO tokenVO = new TokenVO();

        tokenVO.setToken(token);

        tokenVO.setExpiration(UserConstants.TOKEN_TIMEOUT.toString());

        context.setTokenVO(tokenVO);

        return next.handle(context);
    }

    @Override
    public void setNext(LoginHandleChain next) {
        this.next = next;
    }
}

