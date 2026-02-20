package com.summit.chat.service.User.register.Chain.Entity;

import com.summit.chat.Result.Result;

public interface RegisterHandleChain  {
    Result handle(RegisterChainContext chainContext) throws Exception;



    void setNext(RegisterHandleChain chain);
}
