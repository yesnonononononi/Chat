package com.summit.chat.Constants;

import org.jspecify.annotations.NonNull;

public class UserConstants {
    public static final String CACHE_USER_TOKEN = "user:token";
    public static final String DATA_NULL = "输入信息为空";
    public static final @NonNull String CACHE_USER_PROFILE_HASH = "user:profile";
    public static final String PASSWORD_UNRIGHT = "密码不正确或账户不存在";
    public static final String USER_NO_EXIST = "用户不存在";
    public static final long NAME_TIMEOUT = 24L;
    public static final long PHONE_TIMEOUT = 24L;
    public static final Long TOKEN_TIMEOUT = 60L;
    public static final String ILLEGAL_CHAR = "非法参数";
    public static final String USER_EXIST = "用户已存在";
    public static final String CODE_ERROR = "验证码错误";
    public static final Long CACHE_PASS_TIMEOUT = 30L;
    public static final boolean USER_LOGIN_OR_REGISTER_AFTER_CODE = true;
    public static final String USER_NAME_PREFIX = "user_";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String UN_SUPPORT_TYPE = "不支持的登录类型";
    public static final Integer STATUS_OK = 1;
    public static final Integer IS_DELETE = 2;

    public static final String DEFAULT_NICKNAME_PREFIX = "chat用户";
    public static final String ILLEGAL_OPERATE = "越权操作";

    public static final String NOT_SUPPORT_YET = "暂不支持";
    public static final String USER_IS_BUSY = "用户正忙";
    public static final String USER_OFFLINE = "对方不在线";
    public static final int DEFAULT_MAX_NICK_LENGTH = 20;
    public static final String NICK_TOO_LONG = "用户名不能超过"+DEFAULT_MAX_NICK_LENGTH+"个字符";
    public static final int DEFAULT_MAX_PW_LENGTH = 18;
    public static final int DEFAULT_MIN_PW_LENGTH = 6;
    public static final String PW_TOO_LONG = "密码长度不能超过"+DEFAULT_MAX_PW_LENGTH+"个字符"+"小于"+DEFAULT_MIN_PW_LENGTH+"个字符";
    public static final String AGE_ILLEGAL = "年龄非法";
    public static final String GENDER_ILLEGAL = "性别非法";
    public static final Integer BLACK_LIST_STATUS = 0;
    public static final String USER_IN_BLACK_LIST = "用户处于禁用状态,如有疑问请联系管理员";
    public static final String PW_ERROR = "密码错误";
    public static final int DEFAULT_VERIFY_CODE_LENGTH = 6;
    public static final String PHONE_INVALID = "手机号格式错误";
    public static final String SET_NOT_SUPER_ADMIN = "用户不存在或者已经是管理员了";
}
