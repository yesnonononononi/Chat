package com.summit.chat.service.User.Login.Chain;


import com.summit.chat.Result.Result;
import com.summit.chat.service.User.Login.Chain.Entity.LoginContext;

public interface LoginHandleChain {
    public Result handle(LoginContext context) throws Exception;
    public void setNext(LoginHandleChain next);
}
