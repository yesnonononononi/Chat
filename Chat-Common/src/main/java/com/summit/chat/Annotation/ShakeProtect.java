package com.summit.chat.Annotation;

import com.summit.chat.Constants.ShakeConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//接口防抖,幂等性,存储唯一
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ShakeProtect {
    //幂等号,全局唯一
    String value();
    //幂等时间
    String ttl() default ShakeConstants.CACHE_EXPIRE_SECONDS;


}