package com.summit.chat.service.User.register.Chain.node;

import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Exception.ChainConfigException;
import com.summit.chat.Result.Result;
import com.summit.chat.model.vo.TokenVO;
import com.summit.chat.service.Impl.Support.TokenSupport;
import com.summit.chat.service.User.register.Chain.Entity.RegisterChainContext;
import com.summit.chat.service.User.register.Chain.Entity.RegisterHandleChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Scope("prototype")
public class TokenGenerateNode implements RegisterHandleChain {
    private RegisterHandleChain next;
    TokenSupport tokenSupport;

    public TokenGenerateNode(TokenSupport tokenSupport) {
        this.tokenSupport = tokenSupport;
    }

    @Override
    public Result handle(RegisterChainContext context) throws Exception {
        context.validate(next);

        String token = tokenSupport.builder()
                .generate(UserConstants.CACHE_USER_TOKEN)
                .expire(UserConstants.TOKEN_TIMEOUT, TimeUnit.MINUTES)
                .build();

        TokenVO tokenVO = context.getTokenVO();

        if(tokenVO == null){
            log.error("【用户注册】自动注册责任链配置错误");
            throw new ChainConfigException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }

        tokenVO.setToken(token);


        return next.handle(context);
    }

    @Override
    public void setNext(RegisterHandleChain next) {
        this.next = next;
    }
}

