package com.summit.chat.service.User.Login.Chain.Node;

import cn.hutool.core.bean.BeanUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Dto.UserDTO;
import com.summit.chat.Exception.ChainConfigException;
import com.summit.chat.Mapper.Mysql.UserMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.mysql.User;
import com.summit.chat.model.vo.TokenVO;
import com.summit.chat.model.vo.UserVO;
import com.summit.chat.service.User.Login.Chain.Entity.LoginContext;
import com.summit.chat.service.User.Login.Chain.LoginHandleChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Slf4j
@Component("ResultGenerateNodeForLogin")
@Scope("prototype")
public class ResultGenerateNodeForLogin implements LoginHandleChain {
    private final UserMapper userMapper;
    public LoginHandleChain next;

    public ResultGenerateNodeForLogin(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    /**
     * 将userVO封装到tokenVO
     * @param context
     * @return
     */
    @Override
    public Result handle(LoginContext context) throws Exception {
        context.validate(next);
        UserVO userVO = new UserVO();
        User user = context.getUser();
        TokenVO tokenVO = context.getTokenVO();
        if (user == null||tokenVO==null) {
            throw new ChainConfigException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }
        BeanUtil.copyProperties(user, userVO);

        tokenVO.setUserVO(userVO);

        return next.handle(context);
    }

    @Override
    public void setNext(LoginHandleChain next) {
        this.next = next;
    }
}

