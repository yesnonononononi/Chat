package com.summit.chat.service.User.register.Chain.node;

import cn.hutool.core.util.StrUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Dto.NormalRegisterLoginRequest;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.EncryptUtil;
import com.summit.chat.Utils.RSAUtil;
import com.summit.chat.service.Impl.Support.User.UserValidator;
import com.summit.chat.service.User.register.Chain.Entity.RegisterChainContext;
import com.summit.chat.service.User.register.Chain.Entity.RegisterHandleChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PwServiceNode implements RegisterHandleChain {
    RegisterHandleChain next;

    @Value("${rsa.private-key}")
    private String privateKey;

    @Autowired
    private UserValidator userValidator;

    @Override
    public Result handle(RegisterChainContext chainContext) throws Exception {
        try {
            chainContext.validate(next);
            NormalRegisterLoginRequest request = chainContext.getRequest();
            String pw = request.getPw();
            if(StrUtil.isNotBlank(pw)){
                // 1. RSA 解密
                String decryptedPw = RSAUtil.decrypt(pw, privateKey);
                
                // 2. 校验密码长度
                userValidator.checkPwAndNick(decryptedPw, null);

                // 3. BCrypt 哈希加密
                String encrypt = EncryptUtil.encrypt(decryptedPw);
                request.setPw(encrypt);
            }
            return next.handle(chainContext);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("【用户注册】密码处理时出现异常: {}", e.getMessage(), e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
    }

    @Override
    public void setNext(RegisterHandleChain next) {
            this.next = next;
    }
}
