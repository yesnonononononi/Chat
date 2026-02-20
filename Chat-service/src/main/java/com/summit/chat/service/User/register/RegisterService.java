package com.summit.chat.service.User.register;

import com.summit.chat.Dto.NormalRegisterLoginRequest;
import com.summit.chat.Result.Result;

public interface RegisterService {
    Result execute(NormalRegisterLoginRequest request) throws Exception;
}
