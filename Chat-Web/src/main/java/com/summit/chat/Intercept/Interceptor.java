package com.summit.chat.Intercept;

import com.summit.chat.Constants.AdminConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Enum.UserRoleEnum;
import com.summit.chat.Mapper.Cache.RedisProcessor;
import com.summit.chat.Mapper.UserMapper;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Interceptor implements HandlerInterceptor {
    RedisProcessor processor;
    UserMapper userMapper;


    public Interceptor(RedisProcessor processor,UserMapper userMapper) {
        this.processor = processor;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 检查是否为排除路径
        String requestURI = request.getRequestURI();
        System.out.println(requestURI);
        if (requestURI.equals("/user/login") || requestURI.equals("/user/register") || requestURI.equals("/home") || requestURI.equals("/")) {
            return true; // 直接放行
        }
        String token = request.getHeader("Authorization");

        if (StringUtil.isBlank(token)) {
            response.setStatus(401);
            return false;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String t = UserConstants.CACHE_USER_TOKEN + ":" + token;

        //查redis,如果存在用户信息,放行
        String userid = processor.get(t, "USERID");
        String role = processor.get(t, "ROLE");
        if (userid == null) {
            log.warn("【认证拦截】token存在,但是信息为空");
            response.setStatus(401);
            return false;
        }
        try {
            UserHolder.setUserID(userid);
            if (role != null) {
                UserHolder.setRole(role);
            }
        } catch (ClassCastException cce) {
            log.error("【认证拦截】拦截器类型转化错误: {}", cce.getMessage());
            response.setStatus(500);
            return false;
        }


        //续命
        processor.expire(t, UserConstants.TOKEN_TIMEOUT, TimeUnit.MINUTES);

        if (requestURI.startsWith("/admin")) {
            User userById = userMapper.getUserById(Long.valueOf(userid));
            if (userById == null) {
                log.warn("【认证拦截】用户不存在");
                response.setStatus(403);
                return false;
            }
            if (UserRoleEnum.USER.getRole().equals(userById.getRole())) {
                log.warn("【认证拦截】用户不是管理员");
                response.setStatus(403);
                return false;
            }
            if (!userid.equals(userById.getId())) {
                log.warn("【认证拦截】用户:{}越权访问", userid);
                response.setStatus(403);
                return false;
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) throws Exception {
        UserHolder.remove();
    }


}


