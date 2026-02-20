package com.summit.chat.AOP;

import com.summit.chat.Annotation.AutoFill;
import com.summit.chat.Enum.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;

@Slf4j
@Aspect
@Component
public class AutoFillAop {
    private static final int DEFAULT_IDX = 0;
    private final String SET_CREATE_TIME = "setCreateTime";
    private final String SET_UPDATE_TIME = "setUpdateTime";
    private final String ARGV_ERROR = "自动填充参数配置错误";

    @Pointcut("@annotation(com.summit.chat.Annotation.AutoFill)")
    private void autoFill() {
    }


    @Around("autoFill()")
    public Object autoFilled(ProceedingJoinPoint joinPoint) throws Throwable {
        // 新增日志：获取目标方法名，用于后续所有日志标注
        String targetMethodName = joinPoint.getSignature().getName();
        log.info("【自动填充】开始执行，目标方法：{}", targetMethodName); // 新增日志：入口日志

        MethodSignature signature = getSignature(joinPoint);
        Object[] args = getArgs(joinPoint);
        try {
            //获取注解值(插入操作||更新操作),插入操作: 设置createTime,updateTime  ,更新操作:设置updateTime
            Method method = getMethod(signature);
            OperationType valFromAnno = getValFromAnno(method);
            int objIdxFormAnno = getObjIdxFormAnno(method);

            // 新增日志：注解解析成功，打印核心配置
            log.info("【自动填充】解析注解成功，目标方法：{}，操作类型：{}，目标参数索引：{}",
                    targetMethodName, valFromAnno, objIdxFormAnno);

            if (valFromAnno.equals(OperationType.INSERT)) {
                handleInsert(args[objIdxFormAnno]);
                // 新增日志：插入操作填充完成
                log.info("【自动填充】目标方法：{}，插入操作执行完成，已为实体填充createTime、updateTime", targetMethodName);
            } else if (valFromAnno.equals(OperationType.UPDATE)) {
                handleUpdate(args[objIdxFormAnno]);
                // 新增日志：更新操作填充完成
                log.info("【自动填充】目标方法：{}，更新操作执行完成，已为实体填充updateTime", targetMethodName);
            } else {
                log.error("【自动填充】配置错误，目标方法：{}，不支持的操作类型：{}", targetMethodName, valFromAnno); // 新增日志：细化错误
                throw new IllegalArgumentException(ARGV_ERROR);
            }
            //执行原方法
            Object result = joinPoint.proceed();
            log.info("【自动填充】目标方法：{}，整体执行完成，原方法已正常调用", targetMethodName); // 新增日志：执行完成兜底
            return result;
        } catch (NoSuchMethodException e) {
            log.error("【自动填充】实体类无对应的方法：{}", e.getMessage());
            log.error("【自动填充】执行失败，目标方法：{}，异常信息：{}", targetMethodName, e.getMessage(), e); // 新增日志：细化异常日志
            return joinPoint.proceed();
        } catch (Exception e){
            log.error("【自动填充】执行失败，目标方法：{}，异常信息：{}", targetMethodName, e.getMessage(), e);
            return joinPoint.proceed();
        }
    }

    private MethodSignature getSignature(ProceedingJoinPoint joinPoint) {
        return (MethodSignature) joinPoint.getSignature();
    }

    private Object[] getArgs(ProceedingJoinPoint joinPoint) {
        return joinPoint.getArgs();
    }

    private Method getMethod(MethodSignature signature) {
        return signature.getMethod();
    }

    private OperationType getValFromAnno(Method method) {
        AutoFill annotation = method.getAnnotation(AutoFill.class);
        return annotation.type();
    }

    private void handleInsert(Object entity) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> aClass = entity.getClass();

        Method method = aClass.getMethod(SET_CREATE_TIME, Timestamp.class);
        Method method1 = aClass.getMethod(SET_UPDATE_TIME, Timestamp.class);
        set(entity, method1);
        set(entity, method);
        // 新增日志：标注具体填充的实体类
        log.debug("【自动填充-插入】已为实体类：{}，执行createTime、updateTime反射填充", aClass.getSimpleName());
    }

    private void handleUpdate(Object entity) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> aClass = entity.getClass();
        Method method = aClass.getMethod(SET_UPDATE_TIME, Timestamp.class);
        set(entity, method);
        // 新增日志：标注具体填充的实体类
        log.debug("【自动填充-更新】已为实体类：{}，执行updateTime反射填充", aClass.getSimpleName());
    }

    private void set(Object entity, Method method) throws InvocationTargetException, IllegalAccessException {
        //获取当前时间戳
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        method.invoke(entity, timestamp);
        // 新增日志：调试级别，打印具体填充的方法和时间戳
        log.debug("【自动填充-反射】执行方法：{}，为实体填充时间戳：{}", method.getName(), timestamp);
    }

    private int getObjIdxFormAnno(Method method) {
        AutoFill annotation = method.getAnnotation(AutoFill.class);
        int idx = annotation.objIdx();
        int finalIdx = idx >= 0 ? idx : DEFAULT_IDX;
        // 新增日志：参数索引兜底处理，打印配置值和最终值
        log.debug("【自动填充-索引】注解配置参数索引：{}，最终使用索引：{}（小于0则使用默认值0）", idx, finalIdx);
        return finalIdx;
    }
}