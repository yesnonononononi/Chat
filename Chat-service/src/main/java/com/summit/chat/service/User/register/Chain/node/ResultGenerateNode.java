package com.summit.chat.service.User.register.Chain.node;

import cn.hutool.core.bean.BeanUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.NormalRegisterLoginRequest;
import com.summit.chat.Result.Result;
import com.summit.chat.model.vo.TokenVO;
import com.summit.chat.model.vo.UserVO;
import com.summit.chat.service.User.register.Chain.Entity.RegisterChainContext;
import com.summit.chat.service.User.register.Chain.Entity.RegisterHandleChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j

public class ResultGenerateNode implements RegisterHandleChain {
    RegisterHandleChain next;
    @Override
    public Result handle(RegisterChainContext chainContext) {
        try {
            //结果生成
            UserVO userVO = new UserVO();

            NormalRegisterLoginRequest request = chainContext.getRequest();

            BeanUtil.copyProperties(request, userVO);

            userVO.setId(chainContext.getUserId());

            userVO.setStatus(UserConstants.STATUS_OK);

            TokenVO tokenVO = new TokenVO();

            tokenVO.setUserVO(userVO);

            chainContext.setTokenVO(tokenVO);

            return next == null ?  Result.ok(tokenVO) : next.handle(chainContext);
        } catch (Exception e) {
            log.error("【用户注册】生成结果时出现异常: {}", e.getMessage(), e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
    }

    @Override
    public void setNext(RegisterHandleChain next) {
        this.next =next;
    }
}

