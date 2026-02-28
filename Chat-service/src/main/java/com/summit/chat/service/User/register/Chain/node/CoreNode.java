package com.summit.chat.service.User.register.Chain.node;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.FileConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.NormalRegisterLoginRequest;
import com.summit.chat.Mapper.UserMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.GlobalIDWorker;
import com.summit.chat.model.entity.User;
import com.summit.chat.service.User.register.Chain.Entity.RegisterChainContext;
import com.summit.chat.service.User.register.Chain.Entity.RegisterHandleChain;
import io.dogakday.NicknameGenerator;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Slf4j

@Component
public class CoreNode implements RegisterHandleChain {
    RegisterHandleChain next;

    UserMapper userMapper;

    public CoreNode(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Result handle(RegisterChainContext chainContext) throws Exception {
        try {
            chainContext.validate(next);
            User user = chainContext.getUser();
            //执行注册
            userMapper.register(user);

            return next.handle(chainContext);
        } catch (DuplicateKeyException e) {
            log.error("【用户注册】用户已存在", e);
            return Result.fail(UserConstants.USER_EXIST);
        } catch (Exception e) {
            log.error("【用户注册】手机号用户注册出现问题: {}", e.getMessage(),e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
    }

    @Override
    public void setNext(RegisterHandleChain next) {
        this.next = next;
    }



}

