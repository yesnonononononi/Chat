package com.summit.chat.service.Impl.Support.Admin;

import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Enum.UserRoleEnum;
import com.summit.chat.Mapper.UserMapper;
import com.summit.chat.model.entity.User;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AdminValidator extends GlobalValidatorImpl<String> {
    @Autowired
    private UserMapper userMapper;

    public  boolean isSuperAdmin(String userId) {
        User user = userMapper.getUserById(Long.valueOf(userId));
        return user != null && user.getRole().equals(UserRoleEnum.SUPER_ADMIN.getRole());
    }

    public boolean isSuperAdminOrAdmin(String userId) {
        User user = userMapper.getUserById(Long.valueOf(userId));
        return user!= null && (user.getRole().equals(UserRoleEnum.SUPER_ADMIN.getRole()) || user.getRole().equals(UserRoleEnum.ADMIN.getRole()));
    }

    @Override
    public boolean validate(String dto) {
        return false;
    }

    public boolean isAdmin(String userID) {
        User userById = userMapper.getUserById(Long.valueOf(userID));
        return userById != null && userById.getRole().equals(UserRoleEnum.ADMIN.getRole());
    }
}
