package com.summit.chat.service.Impl;

import com.summit.chat.Exception.BusinessException;
import com.summit.chat.GlobalHandle.Validator.GlobalValidator;

/*
* 全局校验适配器
* */
public abstract class GlobalValidatorImpl<T> implements GlobalValidator<T> {
    public void throwException(String s){
        throw new BusinessException(s);
    }


}
