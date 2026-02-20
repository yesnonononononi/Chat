package com.summit.chat.service.User.Login.Chain.Factory;

import com.summit.chat.service.User.Login.Chain.Entity.NodeTypeOfLogin;
import com.summit.chat.service.User.Login.Chain.LoginHandleChain;

import java.util.List;

public interface NodeFactoryTemplate {

    LoginHandleChain createNode(NodeTypeOfLogin nodeType);


    LoginHandleChain assembleNode(List<NodeTypeOfLogin> list);
}
