package com.summit.chat.service.User.register.Chain.node;

import cn.hutool.core.bean.BeanUtil;
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
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
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
        chainContext.validate(next);
        NormalRegisterLoginRequest request = chainContext.getRequest();
        User user = new User();
        try {
            BeanUtil.copyProperties(request, user);

            String userID = GlobalIDWorker.generateId();

            user.setId(userID);

            user.setIcon(FileConstants.DEFAULT_FILE);

            if(StringUtil.isBlank(user.getNickName())) user.setNickName(UserConstants.DEFAULT_NICKNAME_PREFIX + user.getId());

            //执行注册
            userMapper.register(user);

            chainContext.setUserId(userID);

            return next.handle(chainContext);
        } catch (DuplicateKeyException e) {
            log.error("【用户注册】用户已存在", e);
            return Result.fail(UserConstants.USER_EXIST);
        } catch (Exception e) {
            log.error("【用户注册】手机号用户注册出现问题: {}", e.getMessage());
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
    }

    @Override
    public void setNext(RegisterHandleChain next) {
        this.next = next;
    }
}

