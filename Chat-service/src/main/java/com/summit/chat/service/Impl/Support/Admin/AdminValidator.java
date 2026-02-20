package com.summit.chat.service.Impl.Support.Admin;

import com.summit.chat.Enum.UserRoleEnum;
import com.summit.chat.Mapper.UserMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.EncryptUtil;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.entity.User;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class AdminValidator extends GlobalValidatorImpl<String> {

    private final UserMapper userMapper;

    public AdminValidator(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public boolean validate(String dto) {
        return false;
    }
}
