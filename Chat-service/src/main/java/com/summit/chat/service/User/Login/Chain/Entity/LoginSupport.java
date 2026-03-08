package com.summit.chat.service.User.Login.Chain.Entity;

import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.NormalLoginRequest;
import com.summit.chat.Dto.VerifyCodeLoginRequest;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.Mysql.Cache.RedisProcessor;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.AliyunUtil;
import com.summit.chat.service.Impl.Support.User.UserSupport;
import com.summit.chat.service.Impl.Support.User.UserValidator;
import com.summit.chat.service.User.Login.Chain.Factory.NodeFactoryTemplate;
import com.summit.chat.service.User.Login.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LoginSupport implements LoginService {

    UserValidator userValidator;
    RedisProcessor redisProcessor;
    AliyunUtil aliyunUtil;
    NodeFactoryTemplate nodeFactoryForLogin;

    public LoginSupport(AliyunUtil aliyunUtil, RedisProcessor redisProcessor, NodeFactoryTemplate nodeFactoryForLogin, UserSupport userSupport, UserValidator userValidator) {
        this.aliyunUtil = aliyunUtil;
        this.redisProcessor = redisProcessor;
        this.nodeFactoryForLogin = nodeFactoryForLogin;
        this.userValidator = userValidator;
    }


    //登录整体流程
    /*
     *   1,空值校验 - 数据库校验 - 密码校验 - 缓存重入
     *   2,空值校验 - 验证码校验 - 数据库校验 - 缓存重入
     * */

    public Result loginByCode(VerifyCodeLoginRequest request) throws Exception {

        LoginContext loginContext = new LoginContext();

        loginContext.setLoginMode(LoginMode.VERIFYCODE);
        loginContext.setCodeLoginRequest(request);
        return nodeFactoryForLogin.assembleNode(
                List.of(NodeTypeOfLogin.NULLVALIDATE,
                        NodeTypeOfLogin.CODEVALIDATE,
                        NodeTypeOfLogin.DBVALIDATE,
                        NodeTypeOfLogin.TOKENGENERATE,
                        NodeTypeOfLogin.RESULTGENERATE,
                        NodeTypeOfLogin.IPADDRESS,
                        NodeTypeOfLogin.CACHESET
                )).handle(loginContext);
    }

    public Result loginByPw(NormalLoginRequest request) throws Exception {
        LoginContext loginContext = new LoginContext();


        loginContext.setLoginMode(LoginMode.PW);
        loginContext.setRequest(request);
        return nodeFactoryForLogin.assembleNode(
                List.of(NodeTypeOfLogin.NULLVALIDATE,
                        NodeTypeOfLogin.DBVALIDATE,
                        NodeTypeOfLogin.PWVALIDATE,
                        NodeTypeOfLogin.TOKENGENERATE,
                        NodeTypeOfLogin.RESULTGENERATE,
                        NodeTypeOfLogin.IPADDRESS,
                        NodeTypeOfLogin.CACHESET
                )).handle(loginContext);


    }

    @Override
    public Result sendCode(String phoneNumber) {
        try {
            //验证手机号
            userValidator.phoneCheck(phoneNumber);
            aliyunUtil.send(phoneNumber);
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【验证码发送】验证码发送出现问题:{}, 错误:{}", phoneNumber, e.getMessage(), e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
    }

    @Override
    public Result loginOut(String token) {
        redisProcessor.deleteHash(UserConstants.CACHE_USER_TOKEN + ":" + token);
        return Result.ok();
    }

}
