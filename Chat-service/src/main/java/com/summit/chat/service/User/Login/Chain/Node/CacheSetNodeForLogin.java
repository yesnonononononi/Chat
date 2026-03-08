package com.summit.chat.service.User.Login.Chain.Node;

import cn.hutool.core.util.StrUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.UserDTO;
import com.summit.chat.Exception.ChainConfigException;
import com.summit.chat.Exception.DataException;
import com.summit.chat.Mapper.Mysql.Cache.CacheSupport;
import com.summit.chat.Result.Result;
import com.summit.chat.model.vo.TokenVO;
import com.summit.chat.model.vo.UserVO;
import com.summit.chat.service.User.Login.Chain.Entity.LoginContext;
import com.summit.chat.service.User.Login.Chain.LoginHandleChain;
import com.summit.chat.service.User.UserService;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.Alias;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component("CacheSetForLogin")
@Scope("prototype")
@Alias("LoginCacheSetNode")
public class CacheSetNodeForLogin implements LoginHandleChain {
    private final UserService userService;
    CacheSupport cacheSupport;
    LoginHandleChain next;
    RedisTemplate<String, Object> redisTemplate;
    RedisTemplate<String, String> zSetRedisTemplate;


    public CacheSetNodeForLogin(RedisTemplate<String,Object> redisTemplate, RedisTemplate<String,String>zSetRedisTemplate, CacheSupport cacheSupport, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.cacheSupport = cacheSupport;
        this.zSetRedisTemplate = zSetRedisTemplate;
        this.userService = userService;
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
        //增加用户活跃度
        // 增加用户活跃度，将当前用户的会话ID作为成员，分数加1
        // 如果集合不存在，Redis 会自动创建该有序集合
        // 有序集合的名称为 UserConstants.CACHE_USER_ACTIVE
        zSetRedisTemplate.opsForZSet().incrementScore(UserConstants.CACHE_USER_ACTIVE, userId, 1);
        updateUserIp(Long.parseLong(userId), tokenVO.getUserVO().getIp());
        //缓存重入
        try {
            cacheSupport.executePipelined(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {

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

    private void updateUserIp(Long userId,String ip){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setIp(ip);
        userService.putUser(userDTO);
    }

    @Override
    public void setNext(LoginHandleChain next) {
        this.next = next;
    }
}

