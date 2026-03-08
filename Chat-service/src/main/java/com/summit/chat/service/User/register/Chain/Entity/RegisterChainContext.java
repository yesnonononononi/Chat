package com.summit.chat.service.User.register.Chain.Entity;

import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Dto.NormalRegisterLoginRequest;
import com.summit.chat.Exception.ChainConfigException;
import com.summit.chat.model.entity.mysql.User;
import com.summit.chat.model.vo.TokenVO;
import lombok.Data;

@Data
public class RegisterChainContext {
    private NormalRegisterLoginRequest request;
    private String userId;
    private TokenVO tokenVO;
    private User user;

    public void validate(RegisterHandleChain node) {
        if (node == null) {
            throw new ChainConfigException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }
    }
}
