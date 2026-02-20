package com.summit.chat.service.User.Login.Chain.Entity;

public enum LoginMode {
    VERIFYCODE("验证码登录"),
    PW("密码登录");
    private final  String loginMode;

    LoginMode(String loginMode) {
        this.loginMode = loginMode;
    }
}
