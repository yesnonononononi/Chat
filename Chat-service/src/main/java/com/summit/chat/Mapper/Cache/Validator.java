package com.summit.chat.Mapper.Cache;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Component;

@Component
public class Validator {
    public boolean baseValidate(Object o){
        return ObjectUtil.isNotEmpty(o);
    }


}
