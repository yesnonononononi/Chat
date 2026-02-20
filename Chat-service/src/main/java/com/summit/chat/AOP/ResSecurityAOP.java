package com.summit.chat.AOP;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.RSAUtil;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResSecurityAOP {
    @Value("${rsa.private-key}")
   private String privateKey;

    @Autowired
    private ObjectMapper objectMapper;

    @Pointcut("(execution(* com.summit.chat.Controller..*.*(..)) || execution(* com.summit.chat.GlobalHandle.Validator.GlobalExceptionHandler.*(..))) && !@annotation(com.summit.chat.Annotation.IgnoreSecurity) && !@within(com.summit.chat.Annotation.IgnoreSecurity)")
    public void pointcut() {
    }
    @Around("pointcut()")
    public Object around(@NotNull ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (RedisSystemException e) {
            log.error("【响应保护】Redis系统异常: {}", e.getMessage(), e);
            // Redis异常时返回服务器错误
            return Result.fail(BaseConstants.REDIS_CONNECTION_ERROR);
        } catch (Exception e) {
            log.error("【响应保护】执行目标方法时发生异常", e);
            // 其他异常继续抛出
            throw e;
        }
        
        try {
            // 如果返回值不是Result类型，直接返回（例如ResponseEntity或void）
            if (!(result instanceof Result)) {
                return result;
            }
            Result<Object> data = (Result<Object>) result;

            //拼接参数
            String content = buildParams(data);
            //签名
            String sign = getSign(content);

            data.setSign(sign);

            return data;
        } catch (RedisSystemException e) {
            log.error("【响应保护】签名过程中的Redis异常: {}", e.getMessage(), e);
            // Redis异常时不进行签名，返回原始数据
            return result;
        } catch (Exception e){
            log.error("【响应保护】签名发生错误:{}",result,e);
            // 如果签名失败，不应该让接口报错，而是返回原始数据
            return result;
        }
    }

    private String getSign(String content ) {
        String sign = RSAUtil.sign(content, privateKey);
        log.info("签名结果:{}",sign);
        return sign;
    }

    private String buildParams(Object data) {
        try {
            // 使用ObjectMapper将对象转为ObjectNode
            ObjectNode node = objectMapper.valueToTree(data);
            // 移除sign字段
            node.remove("sign");
            // 转为字符串，确保与Spring MVC返回的JSON格式一致
            return objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            log.error("构建签名参数失败", e);
            throw new RuntimeException("构建签名参数失败", e);
        }
    }


}
