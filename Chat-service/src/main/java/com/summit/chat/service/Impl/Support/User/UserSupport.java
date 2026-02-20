package com.summit.chat.service.Impl.Support.User;

import com.summit.chat.Utils.EncryptUtil;
import org.springframework.stereotype.Component;

@Component
public class UserSupport {
    public String encryptPw(String pw) {
        return EncryptUtil.encrypt(pw);
    }
}
