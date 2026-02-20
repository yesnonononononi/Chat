package com.summit.chat.Annotation;

import com.summit.chat.Enum.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 字段(updateTime,startTime,createTime,自动填充)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    OperationType type();

    /**
     *需要填充的对象位置索引
     */
    int objIdx() default 0;
}
