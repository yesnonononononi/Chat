package com.summit.chat.service.User.register;

import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.LockConstants;
import com.summit.chat.Dto.NormalRegisterLoginRequest;
import com.summit.chat.Result.Result;
import com.summit.chat.service.User.register.Chain.Entity.NodeTypeOfRegister;
import com.summit.chat.service.User.register.Chain.Entity.RegisterChainContext;
import com.summit.chat.service.User.register.Chain.Entity.RegisterHandleChain;
import com.summit.chat.service.User.register.Chain.Factory.NodeFactoryOfRegister;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class RegisterSupport implements RegisterService {

    private final RegisterLockManager lockManager;
    private final NodeFactoryOfRegister nodeFactoryOfRegister;

    @Autowired
    @Lazy
    RegisterSupport handle;

    // 构造器仅注入必要的单例依赖
    public RegisterSupport(NodeFactoryOfRegister nodeFactoryOfRegister, RegisterLockManager lockManager) {
        this.lockManager = lockManager;
        this.nodeFactoryOfRegister = nodeFactoryOfRegister;
    }


    /**
     * 每次调用都构建全新的责任链（所有节点都是新实例）
     */
    private RegisterHandleChain buildNewChain(List<NodeTypeOfRegister> list) {
        return nodeFactoryOfRegister.assembleNode(list);
    }


    public Result execute(NormalRegisterLoginRequest request) throws Exception {
        RLock lock = lockManager.getLock(request.getMobile());
        try {
            return handle.core(request);
        } catch (Exception e) {
            log.error("注册发生问题", e);
            throw e;
        } finally {
            lockManager.unLock(lock);
        }
    }

    public Result execute(String mobile) throws Exception {
        if (mobile == null) {
            log.error("自动注册出现错误,参数为空");
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
        RLock lock = lockManager.getLock(mobile);

        NormalRegisterLoginRequest normalRegisterLoginRequest = new NormalRegisterLoginRequest();

        normalRegisterLoginRequest.setMobile(mobile);
        try {
            return handle.core(normalRegisterLoginRequest);
        } catch (Exception e) {
            log.error("注册发生问题", e);
            throw e;
        } finally {
         lockManager.unLock(lock);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result core(NormalRegisterLoginRequest request) throws Exception {
        RegisterHandleChain headNode;
        //一般注册链
        if (request != null && request.getVerifyCode() == null) {
            headNode = buildNewChain(nodeFactoryOfRegister.templateOfAutoRegisterByLogin());
        } else headNode = buildNewChain(nodeFactoryOfRegister.templateOfNormal());


        RegisterChainContext registerChainContext = new RegisterChainContext();
        registerChainContext.setRequest(request);

        // 执行全新的责任链
        return headNode.handle(registerChainContext);
    }


}