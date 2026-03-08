package com.summit.chat.service.User.Login.Chain.Entity;

public enum NodeTypeOfLogin {
    CACHESET("缓存重入节点"),
    NULLVALIDATE("空值检查节点"),
    DBVALIDATE("数据库校验节点"),
    CODEVALIDATE("验证码校验节点"),
    PWVALIDATE("密码校验节点"),
    TOKENGENERATE("令牌生成节点"),
    RESULTGENERATE("结果封装节点"),
    IPADDRESS("ip地址获取节点");
    public final String nodeType;

    NodeTypeOfLogin(String nodeType) {
        this.nodeType = nodeType;
    }
}
