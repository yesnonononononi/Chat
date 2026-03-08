package com.summit.chat.service.User.Login.Chain.Node;

import cn.hutool.core.util.ObjectUtil;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.NormalLoginRequest;
import com.summit.chat.Dto.VerifyCodeLoginRequest;
import com.summit.chat.Mapper.Mysql.UserMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.mysql.User;
import com.summit.chat.service.User.Login.Chain.Entity.LoginContext;
import com.summit.chat.service.User.Login.Chain.Entity.LoginMode;
import com.summit.chat.service.User.Login.Chain.Factory.NodeFactoryOfLogin;
import com.summit.chat.service.User.Login.Chain.LoginHandleChain;
import com.summit.chat.service.User.register.RegisterSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Scope("prototype")
public class DbValidateNodeForLogin implements LoginHandleChain {
    LoginHandleChain next;
    UserMapper userMapper;
    RegisterSupport registerSupport;
    NodeFactoryOfLogin nodeFactoryOfLogin;

    public DbValidateNodeForLogin(NodeFactoryOfLogin nodeFactoryOfLogin, RegisterSupport registerSupport, UserMapper userMapper) {
        this.userMapper = userMapper;
        this.registerSupport = registerSupport;
        this.nodeFactoryOfLogin = nodeFactoryOfLogin;
    }

    @Override
    public Result handle(LoginContext context) throws Exception {
        User user;
        String mobile = "";
        context.validate(next);
        LoginMode loginMode = context.getLoginMode();

        if (LoginMode.VERIFYCODE == loginMode) {
            VerifyCodeLoginRequest request = context.getCodeLoginRequest();
            mobile = request.getMobile();


        } else if (LoginMode.PW == loginMode) {
            NormalLoginRequest request = context.getRequest();
            mobile = request.getMobile();

        } else {
            return Result.fail(UserConstants.UN_SUPPORT_TYPE);
        }


        //查数据库是否有用户
        user = userMapper.getUserByPhone(mobile);

        if (ObjectUtil.isNull(user)) {
            //用户不存在,决定是否执行注册
            if (UserConstants.USER_LOGIN_OR_REGISTER_AFTER_CODE && LoginMode.VERIFYCODE == loginMode) {

                return registerSupport.execute(mobile);
            }
            return Result.fail(UserConstants.USER_NO_EXIST);
        }
        if (Objects.equals(user.getIsDelete(), UserConstants.IS_DELETE)) {
            return Result.fail(UserConstants.USER_NO_EXIST);
        }
        if(Objects.equals(user.getStatus(), UserConstants.BLACK_LIST_STATUS)) {
            return Result.fail(UserConstants.USER_IN_BLACK_LIST);
        }
        context.setUser(user);
        return next.handle(context);
    }

    @Override
    public void setNext(LoginHandleChain next) {
        this.next = next;
    }
}

