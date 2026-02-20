package com.summit.chat.service.User.register.Chain.node;

import cn.hutool.core.util.ObjectUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Exception.ChainConfigException;
import com.summit.chat.Mapper.Cache.CacheSupport;
import com.summit.chat.Result.Result;
import com.summit.chat.model.vo.TokenVO;
import com.summit.chat.model.vo.UserVO;
import com.summit.chat.service.User.register.Chain.Entity.RegisterChainContext;
import com.summit.chat.service.User.register.Chain.Entity.RegisterHandleChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Scope("prototype")
public class CacheSetNode implements RegisterHandleChain {
    private RegisterHandleChain next;
    CacheSupport cacheSupport;

    public CacheSetNode(CacheSupport cacheSupport) {
        this.cacheSupport = cacheSupport;
    }

    @Override
    public Result handle(RegisterChainContext chainContext) throws Exception {

        TokenVO tokenVO = chainContext.getTokenVO();
        if(tokenVO == null ||ObjectUtil.isEmpty(tokenVO)){
            log.error("【用户注册】tokeVO为空值");
            throw new ChainConfigException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }
        String token = tokenVO.getToken();

        UserVO userVO = tokenVO.getUserVO();

        String id = userVO.getId();
        try {
            // 只缓存Token信息，用户信息由Spring Cache接管（懒加载）
            cacheSupport.put(UserConstants.CACHE_USER_TOKEN + ":" + token, "USERID", id);
            cacheSupport.expire(UserConstants.CACHE_USER_TOKEN, token, UserConstants.TOKEN_TIMEOUT, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("【用户注册】缓存写入错误:{}",id,e);
            Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
        return Result.ok(tokenVO);
    }

    @Override
    public void setNext(RegisterHandleChain next) {
        this.next = next;
    }
}

