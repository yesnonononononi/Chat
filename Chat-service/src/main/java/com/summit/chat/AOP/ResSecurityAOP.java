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
        Object result = joinPoint.proceed();
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
        }catch (Exception e){
            log.error("【响应保护】发生错误:{}",result,e);
            // 如果签名失败，不应该让接口报错，而是返回原始数据（或者根据安全策略决定是否报错）
            // 这里为了业务连续性，暂不阻断，但在日志中记录错误
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
