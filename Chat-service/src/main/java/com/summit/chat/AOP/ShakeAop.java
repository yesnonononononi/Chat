package com.summit.chat.AOP;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.summit.chat.AOP.SPEL.ShakeSPELParser;
import com.summit.chat.Annotation.ShakeProtect;
import com.summit.chat.Constants.ShakeConstants;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.LuaUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
@Aspect
@Component
public class ShakeAop {

    @Resource
    ShakeSPELParser shakeSPELParser;

    @Resource
    LuaUtil luaUtil;



    @Around("@annotation(com.summit.chat.Annotation.ShakeProtect)")
    public Object shakeProcess(@NonNull ProceedingJoinPoint joinPoint) throws Throwable {
        //流程: 方法执行前: 先获取幂等id - 查缓存是否有结果 - 有则返回,结束- 没有执行业务方法-结果缓存 - 设置过期时间
        String id = null;
        Object result;
        log.info("【幂等防护】幂等处理已启动");
        // 提前获取方法签名和返回类型（核心：用于后续类型转换）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetReturnType = signature.getReturnType();
        try {
            id = getValFromAnno(joinPoint);


            String name = signature.getMethod().getName();
            //lua
            List<Object> list = List.of(id);
            // 修复：Lua脚本中 ARGV[1] 是 key 前缀，这里必须包含方法名以保证 key 的唯一性和一致性
            // 之前的 bug：检查时用了 method name，但缓存结果时没用，导致永远读不到结果
            String cacheKeyPrefix = ShakeConstants.SHAKE_LUA_CACHE_PREFIX + name;
            
            String res = luaUtil.execute(
                    list,
                    ShakeConstants.SHAKE_LUA_RESULT_PATH,
                    String.class,
                    cacheKeyPrefix,  //shake:result:id:methodName:<key>
                    ShakeConstants.SHAKE_HOLD_EXPIRE_SECONDS
            );


            switch (res) {
                case ShakeConstants.SHAKE_LUA_RESULT_NONE -> log.info("【幂等防护】缓存未命中{}的结果", id);
                case ShakeConstants.SHAKE_LUA_RESULT_PROCESS -> {
                    log.info("【幂等防护】目前{}已有方法在执行", id);
                    // 幂等保护：已有相同幂等 id 正在处理时，不继续执行业务方法，避免并发穿透
                    return Result.fail(ShakeConstants.SHAKE_LUA_RESULT_PROCESS);
                }
                case null -> log.info("【幂等防护】缓存执行结果为空值");
                default -> {
                    log.info("【幂等防护】幂等防护已生效:id{}", id);
                    return Result.fail(ShakeConstants.REQUEST_REPEAT);
                }
            }

            log.info("【幂等防护】执行业务方法");
            //执行业务方法
            result = joinPoint.proceed(joinPoint.getArgs());

            String ttlFromAnno = getTTLFromAnno(joinPoint);

            //结果缓存并设置过期时间，传入正确的前缀（包含方法名）
            putToCache(id, ttlFromAnno, cacheKeyPrefix);

        } catch (Throwable e) {
            //捕捉到任何错误
            log.error("【幂等防护】幂等防护遇到错误,id:{},已执行业务方法", id, e);
            //清理调无用的HOLD占位符，传入正确的前缀（包含方法名）
            if (id != null) {
                String name = signature.getMethod().getName();
                String cacheKeyPrefix = ShakeConstants.SHAKE_LUA_CACHE_PREFIX + name;
                cleanHoldCache(id, cacheKeyPrefix);
            }
            return joinPoint.proceed(joinPoint.getArgs());
        }

        return result;

    }


    //获取注解值/幂等id
    private String getValFromAnno(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            ShakeProtect annotation = method.getAnnotation(ShakeProtect.class);
            String spel = annotation.value();
            //使用Spring的解析器,避免复杂的spel解析代码
            String id = shakeSPELParser.parse(spel, method, args);

            if (id == null || StrUtil.isBlank(id)) {
                throw new BusinessException(ShakeConstants.ARGV_ERROR);
            }
            return id;
        } catch (Exception e) {
            log.warn("【幂等防护】{}", ShakeConstants.NULL_VAL_OF_ANNO);
            throw e;
        }
    }

    //10秒
    private <T> void putToCache(String id,  String ttl, String cacheKeyPrefix) {
        // 修改逻辑：允许 result 为 null，防止缓存穿透
        if (id == null) {
            return;
        }
        try {
            // 如果 result 为 null，JSONUtil.toJsonStr(null) 返回 "null"（字符串），这正好可以作为缓存值
            String res = ShakeConstants.DEFAULT_HOLD;   //将序列化好后的对象放入缓存,直接将result放入会存储二进制数据,影响性能
            //lua
            List<Object> list = List.of(id);
            // 修复：指定返回类型为 String.class，避免 null resultType
            luaUtil.execute(list, ShakeConstants.SHAKE_LUA_RESULT_PATH_1, String.class,
                    res, cacheKeyPrefix, // 使用传入的正确前缀
                    ttl);
        } catch (Exception e) {
            log.warn("【幂等防护】{}", ShakeConstants.CACHE_SET_ERROR, e);
            //缓存操作不报错,防止主流程结束,却因缓存导致使用了注解的接口都报错
        }
    }


    private void cleanHoldCache(String id, String cacheKeyPrefix) {
        if (id == null || id.isBlank()) {
            return;
        }
        try {
            List<Object> list = List.of(id);
            luaUtil.execute(list, ShakeConstants.SHAKE_LUA_CLEAN_HOLD_PATH, null,
                    cacheKeyPrefix); // 使用传入的正确前缀
        } catch (Exception e) {
            log.warn("【幂等防护】清理执行中占位符HOLD失败, id:{}", id, e);
        }
    }


    private String getTTLFromAnno(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ShakeProtect annotation = method.getAnnotation(ShakeProtect.class);
        return annotation.ttl();
    }
}
