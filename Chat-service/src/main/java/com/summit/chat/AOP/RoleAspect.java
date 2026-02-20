package com.summit.chat.AOP;

import com.summit.chat.Annotation.RequireRole;
import com.summit.chat.Enum.UserRoleEnum;
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
        String requiredRole = requireRole.role().getRole();
        String currentRole = UserHolder.getRole();

        // 1. 如果当前用户无角色信息，直接拒绝
        if (currentRole == null) {
            log.warn("【角色鉴权】用户 {} 无角色信息，尝试访问 {}", UserHolder.getUserID(), joinPoint.getSignature().getName());
            throw new BusinessException("无权限访问");
        }

        // 2. 超级管理员拥有最高权限，直接放行
        if (UserRoleEnum.SUPER_ADMIN.getRole().equals(currentRole)) {
            return joinPoint.proceed();
        }

        // 3. 校验角色是否匹配（这里可以扩展为层级校验，目前保持严格匹配）
        if (!currentRole.equals(requiredRole)) {
            log.warn("【角色鉴权】用户 {} 尝试访问受限接口 {}, 需要角色 {}, 当前角色 {}", UserHolder.getUserID(), joinPoint.getSignature().getName(), requiredRole, currentRole);
            throw new BusinessException("无权限访问");
        }
        
        return joinPoint.proceed();
    }
}
