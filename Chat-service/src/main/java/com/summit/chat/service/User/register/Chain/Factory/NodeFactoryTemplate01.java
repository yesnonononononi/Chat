package com.summit.chat.service.User.register.Chain.Factory;


import com.summit.chat.service.User.register.Chain.Entity.NodeTypeOfRegister;
import com.summit.chat.service.User.register.Chain.Entity.RegisterHandleChain;

import java.util.List;

public interface NodeFactoryTemplate01 {

    public RegisterHandleChain createNode(NodeTypeOfRegister nodeType);

    public RegisterHandleChain assembleNode(List<NodeTypeOfRegister> list);
}
