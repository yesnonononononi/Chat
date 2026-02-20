package com.summit.chat.AOP;

import com.summit.chat.Annotation.RequireRole;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RoleAspect {

    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        String requiredRole = requireRole.value();
        String currentRole = UserHolder.getRole();

        // 如果当前用户没有角色或角色不匹配
        // 注意：这里是一个简单的相等判断。如果需要层级判断（如admin > user），需要修改此处逻辑
        if (currentRole == null || !currentRole.equals(requiredRole)) {
            log.warn("【角色鉴权】用户 {} 尝试访问受限接口 {}, 需要角色 {}, 当前角色 {}", UserHolder.getUserID(), joinPoint.getSignature().getName(), requiredRole, currentRole);
            throw new BusinessException("无权限访问");
        }
        return joinPoint.proceed();
    }
}
