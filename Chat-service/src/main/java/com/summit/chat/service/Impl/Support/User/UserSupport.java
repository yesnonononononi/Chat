package com.summit.chat.service.Impl.Support.User;

import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.UserDTO;
import com.summit.chat.Dto.UserPwPutDto;
import com.summit.chat.Mapper.UserMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.EncryptUtil;
import com.summit.chat.Utils.UserHolder;
import org.springframework.stereotype.Component;

@Component
public class UserSupport {
    private final UserMapper userMapper;

    public UserSupport(UserMapper userMapper ) {
        this.userMapper = userMapper;

    }

    public String encryptPw(String pw) {
        return EncryptUtil.encrypt(pw);
    }


    public Result setPw(UserPwPutDto dto){
        dto.setPw( encryptPw(dto.getPw()));
        Integer i = userMapper.putPw(dto);
        if (i == 1) {
            return Result.ok();
        }

        return Result.fail(UserConstants.USER_NO_EXIST);
    }
}
