package com.summit.chat.service.Impl.Support.UserSupport;

import cn.hutool.core.util.PhoneUtil;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.UserDTO;
import com.summit.chat.Dto.UserPwPutDto;
import com.summit.chat.Mapper.UserMapper;
import com.summit.chat.Utils.AliyunUtil;
import com.summit.chat.Utils.EncryptUtil;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.entity.User;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import jodd.util.StringUtil;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserValidator extends GlobalValidatorImpl<UserDTO> {
    private final UserMapper userMapper;
    private final AliyunUtil aliyunUtil;

    public UserValidator(UserMapper userMapper, AliyunUtil aliyunUtil) {
        super();
        this.userMapper = userMapper;
        this.aliyunUtil = aliyunUtil;
    }

    @Override
    public boolean validate(UserDTO dto) {
        return false;
    }

    public void checkPwAndNick(String pw, String nickName) {
        //校验用户名是否超过限制
        if (nickName != null && (nickName.length() > UserConstants.DEFAULT_MAX_NICK_LENGTH || nickName.isEmpty())) {
            super.throwException(UserConstants.NICK_TOO_LONG);
        }
        //校验密码是否超过限制 6-18位
        if (pw != null && (pw.length() > UserConstants.DEFAULT_MAX_PW_LENGTH || pw.length() < UserConstants.DEFAULT_MIN_PW_LENGTH)) {
            super.throwException(UserConstants.PW_TOO_LONG);
        }
    }

    public void checkAge(Integer age) {
        if (age != null && (age > 100 || age < 0)) {
            super.throwException(UserConstants.AGE_ILLEGAL);
        }
    }

    public void checkGender(Integer gender) {
        if (gender != null && (gender != 1 && gender != 2 && gender != 0)) {
            super.throwException(UserConstants.GENDER_ILLEGAL);
        }
    }

    public void checkAuth(String userID) {
        String userId = UserHolder.getUserID();
        //是否越权访问
        if (userId == null || !userId.equals(userID)) {
            super.throwException(UserConstants.ILLEGAL_OPERATE);
        }
    }

    public void putPwCheck(UserPwPutDto dto) {
        String pw = dto.getPw();
        if (StringUtil.isBlank(pw)) {
            super.throwException(UserConstants.ILLEGAL_CHAR);
        }
        if (pw.length() > UserConstants.DEFAULT_MAX_PW_LENGTH || pw.length() < UserConstants.DEFAULT_MIN_PW_LENGTH) {
            super.throwException(UserConstants.PW_TOO_LONG);
        }
        //获取真实密码
        User userById = userMapper.getUserById(Long.valueOf(Objects.requireNonNull(UserHolder.getUserID())));
        if(!EncryptUtil.identity(pw, userById.getPw())){
            super.throwException(UserConstants.PW_ERROR);
        }
    }

    public void forgetPwCheck(UserPwPutDto dto) throws Exception {
        String mobile = dto.getMobile();
        String verifyCode = dto.getVerifyCode();
        if(mobile == null || verifyCode == null){
            super.throwException(UserConstants.ILLEGAL_CHAR);
        }
        if(mobile.length() != 11) {
            super.throwException(UserConstants.ILLEGAL_CHAR);
        }
        if(verifyCode.length() != UserConstants.DEFAULT_VERIFY_CODE_LENGTH) {
            super.throwException(UserConstants.ILLEGAL_CHAR);
        }
        if (!aliyunUtil.validate(mobile,verifyCode)) {
            super.throwException(UserConstants.CODE_ERROR);
        }
    }

    public void phoneCheck(String mobile) {
        if(!PhoneUtil.isMobile( mobile)){
            super.throwException(UserConstants.PHONE_INVALID);
        }
    }
}
