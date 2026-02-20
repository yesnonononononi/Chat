package com.summit.chat.service.User.register.Chain.Factory;


import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Exception.ChainConfigException;
import com.summit.chat.Exception.NoSuchNodeException;
import com.summit.chat.service.User.register.Chain.Entity.NodeTypeOfRegister;
import com.summit.chat.service.User.register.Chain.Entity.RegisterHandleChain;
import com.summit.chat.service.User.register.Chain.node.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class NodeFactoryOfRegister implements NodeFactoryTemplate01 {
    
    BeanFactory beanFactory;

    public NodeFactoryOfRegister(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public RegisterHandleChain createNode(NodeTypeOfRegister nodeType) {
        //根据节点类型创建节点
        if (nodeType == null) {
            throw new ChainConfigException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }


        RegisterHandleChain result = switch (nodeType) {
            case NodeTypeOfRegister.CODEVALIDATE -> beanFactory.getBean(CodeValidateNode.class);
            case NodeTypeOfRegister.DBVALIDATE -> beanFactory.getBean(CoreNode.class);
            case NodeTypeOfRegister.PW_SERVICE -> beanFactory.getBean(PwServiceNode.class);
            case NodeTypeOfRegister.NULLVALIDATE ->  beanFactory.getBean(NullValidateNode.class);
            case NodeTypeOfRegister.RESULTGENERATE -> beanFactory.getBean(ResultGenerateNode.class);
            case NodeTypeOfRegister.USEREXISTVALIDATE->beanFactory.getBean(UserExistValidateNode.class);
            case NodeTypeOfRegister.CACHESET->beanFactory.getBean(CacheSetNode.class);
            case NodeTypeOfRegister.TOKENGENERATE -> beanFactory.getBean(TokenGenerateNode.class);
            default -> throw new NoSuchNodeException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        };
        return result;
    }

    @Override
    public RegisterHandleChain assembleNode(List<NodeTypeOfRegister> list) {

        if (list == null || list.isEmpty()) {
            log.error("列表不能为空");
            throw new IllegalArgumentException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }

        RegisterHandleChain lastNode = null, headNode = null;

        //遍历列表
        for (NodeTypeOfRegister nodeType : list) {
            RegisterHandleChain node = createNode(nodeType);
            if (lastNode != null) lastNode.setNext(node);  //上一个节点的下一个节点就是当前节点
            else headNode = node;
            lastNode = node;
        }

        return headNode;
    }
    public List<NodeTypeOfRegister> templateOfNormal(){
        return List.of(
                NodeTypeOfRegister.NULLVALIDATE,
                NodeTypeOfRegister.CODEVALIDATE,
                NodeTypeOfRegister.USEREXISTVALIDATE,
                NodeTypeOfRegister.PW_SERVICE,
                NodeTypeOfRegister.DBVALIDATE,
                NodeTypeOfRegister.RESULTGENERATE
        );
    }
    //数据库校验 - 结果封装 - 令牌生成  - 缓存
    public List<NodeTypeOfRegister> templateOfAutoRegisterByLogin(){
        return List.of(
                NodeTypeOfRegister.DBVALIDATE,
                NodeTypeOfRegister.RESULTGENERATE,
                NodeTypeOfRegister.TOKENGENERATE,
                NodeTypeOfRegister.CACHESET
        );
    }




}
