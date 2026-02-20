package com.summit.chat.service.User.Login.Chain.Node;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Exception.ChainConfigException;
import com.summit.chat.Exception.DataException;
import com.summit.chat.Mapper.Cache.CacheSupport;
import com.summit.chat.Result.Result;
import com.summit.chat.model.vo.TokenVO;
import com.summit.chat.model.vo.UserVO;
import com.summit.chat.service.User.Login.Chain.Entity.LoginContext;
import com.summit.chat.service.User.Login.Chain.LoginHandleChain;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.Alias;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component("CacheSetForLogin")
@Scope("prototype")
@Alias("LoginCacheSetNode")
public class CacheSetNodeForLogin implements LoginHandleChain {
    CacheSupport cacheSupport;
    LoginHandleChain next;


    public CacheSetNodeForLogin(CacheSupport cacheSupport) {
        this.cacheSupport = cacheSupport;
    }

    /**
     * 将令牌,用户信息放入缓存
     */
    @Override
    public Result handle(LoginContext context) {
        TokenVO tokenVO = context.getTokenVO();

        UserVO userVO = tokenVO.getUserVO();

        String token = tokenVO.getToken();

        if(StringUtil.isBlank(token)||userVO == null){
            throw new ChainConfigException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }

        String userId = userVO.getId();

        if(userId == null){
            log.error("【用户登录】用户不存在的id:{}", context.getRequest().getMobile());
            throw new DataException(BaseConstants.SERVER_EXCEPTION);
        }

        //缓存重入
        try {
            cacheSupport.executePipelined(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    // 1. 缓存用户基本信息
                    String profileKey = UserConstants.CACHE_USER_PROFILE_HASH + ":" + userId;
                    Map<String, Object> map = BeanUtil.beanToMap(userVO);
                    map.replaceAll((key, value) -> StrUtil.toStringOrEmpty(value));
                    operations.opsForHash().putAll(profileKey, map);
                    operations.expire(profileKey, UserConstants.NAME_TIMEOUT, TimeUnit.HOURS);

                    // 2. 缓存Token信息
                    String tokenKey = UserConstants.CACHE_USER_TOKEN + ":" + tokenVO.getToken();
                    operations.opsForHash().put(tokenKey, "USERID", userId);
                    if (userVO.getRole() != null) {
                        operations.opsForHash().put(tokenKey, "ROLE", StrUtil.toString(userVO.getRole()));
                    }
                    operations.expire(tokenKey, UserConstants.TOKEN_TIMEOUT, TimeUnit.MINUTES);
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("【用户登录】缓存放入失败: {}", e.getMessage(), e);
        }
        return Result.ok(tokenVO);
    }

    @Override
    public void setNext(LoginHandleChain next) {
        this.next = next;
    }
}

