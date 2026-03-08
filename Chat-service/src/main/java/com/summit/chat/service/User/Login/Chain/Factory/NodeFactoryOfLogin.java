package com.summit.chat.service.User.Login.Chain.Factory;

import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Exception.ChainConfigException;
import com.summit.chat.Exception.NoSuchNodeException;
import com.summit.chat.service.User.Login.Chain.Entity.NodeTypeOfLogin;
import com.summit.chat.service.User.Login.Chain.LoginHandleChain;
import com.summit.chat.service.User.Login.Chain.Node.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Slf4j
public class NodeFactoryOfLogin implements NodeFactoryTemplate {
   BeanFactory beanFactory;

    public NodeFactoryOfLogin(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 创建对应节点
     *
     * @param nodeType 节点类型
     * @return 节点
     */
    @Override
    public LoginHandleChain createNode(NodeTypeOfLogin nodeType) {
        //根据节点类型创建节点
        if (nodeType == null) {
            throw new ChainConfigException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }


        LoginHandleChain result = switch (nodeType) {
            case NodeTypeOfLogin.CACHESET -> beanFactory.getBean(CacheSetNodeForLogin.class);
            case NodeTypeOfLogin.CODEVALIDATE -> beanFactory.getBean(CodeValidateNodeForLogin.class);
            case NodeTypeOfLogin.DBVALIDATE -> beanFactory.getBean(DbValidateNodeForLogin.class);
            case NodeTypeOfLogin.NULLVALIDATE ->  beanFactory.getBean(NullValidateNodeForLogin.class);
            case NodeTypeOfLogin.PWVALIDATE -> beanFactory.getBean(PwValidateNodeForLogin.class);
            case NodeTypeOfLogin.TOKENGENERATE -> beanFactory.getBean(TokenGenerateNodeForLogin.class);
            case NodeTypeOfLogin.RESULTGENERATE -> beanFactory.getBean(ResultGenerateNodeForLogin.class);
            case NodeTypeOfLogin.IPADDRESS -> beanFactory.getBean(IpAddressNodeForLogin.class);
            default -> throw new NoSuchNodeException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        };
        return result;
    }




    @Override
    public LoginHandleChain assembleNode(List<NodeTypeOfLogin> list) {

        if (list == null || list.isEmpty()) {
            log.error("列表不能为空");
            throw new IllegalArgumentException(BaseConstants.CHAIN_CONFIG_EXCEPTION);
        }

        LoginHandleChain lastNode = null, headNode = null;

        //遍历列表
        for (NodeTypeOfLogin nodeType : list) {
            LoginHandleChain node = createNode(nodeType);
            if (lastNode != null) lastNode.setNext(node);  //上一个节点的下一个节点就是当前节点
            else headNode = node;
            lastNode = node;
        }

        return headNode;
    }

}
