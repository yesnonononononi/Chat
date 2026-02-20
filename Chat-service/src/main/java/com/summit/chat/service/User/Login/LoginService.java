package com.summit.chat.service.User.Login;

import com.summit.chat.Dto.NormalLoginRequest;
import com.summit.chat.Dto.VerifyCodeLoginRequest;
import com.summit.chat.Result.Result;

public interface LoginService {
    Result loginByCode(VerifyCodeLoginRequest request) throws Exception;
    Result loginByPw(NormalLoginRequest request)throws Exception;
    Result sendCode(String phoneNumber);
    Result loginOut(String token);
}
