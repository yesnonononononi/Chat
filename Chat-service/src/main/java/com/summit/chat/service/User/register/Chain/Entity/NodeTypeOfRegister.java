package com.summit.chat.service.User.register.Chain.Entity;

public enum NodeTypeOfRegister{
    NULLVALIDATE("空值检查节点"),
    DBVALIDATE("数据库校验节点"),
    CODEVALIDATE("验证码校验节点"),
    PW_SERVICE("密码服务节点"),
    RESULTGENERATE("结果封装节点"),
    USEREXISTVALIDATE("用户存在校验节点"),
    CACHESET("缓存操作节点"),
    TOKENGENERATE("令牌生成");

    public final String nodeType;

    NodeTypeOfRegister(String nodeType) {
        this.nodeType = nodeType;
    }
}
