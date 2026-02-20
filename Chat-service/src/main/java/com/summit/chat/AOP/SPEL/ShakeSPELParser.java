package com.summit.chat.AOP.SPEL;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class ShakeSPELParser {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 解析SPEL表达式
     * @param spel SPEL表达式
     * @param method 目标方法
     * @param args 方法参数
     * @return 解析后的值
     */
    public String parse(String spel, Method method, Object[] args) {
        if (spel == null || spel.trim().isEmpty()) {
            return null;
        }
        
        // 如果不包含#且不包含T(，说明不是SPEL表达式，直接返回原始字符串作为Key
        if (!spel.contains("#") && !spel.contains("T(")) {
            return spel;
        }

        try {
            // 使用Spring的MethodBasedEvaluationContext，它自动处理参数名绑定
            EvaluationContext context = new MethodBasedEvaluationContext(null, method, args, parameterNameDiscoverer);
            
            // 解析表达式
            Object value = parser.parseExpression(spel).getValue(context);
            
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.error("【SPEL解析】SPEL解析失败: {}", spel, e);
            throw new RuntimeException("SPEL解析失败", e);
        }
    }
}
