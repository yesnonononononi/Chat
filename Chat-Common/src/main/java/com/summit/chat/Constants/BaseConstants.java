package com.summit.chat.Constants;

public class BaseConstants {
    public static final String SERVER_EXCEPTION = "服务繁忙,请重试";
    public static final String REDIS_CONNECTION_ERROR = "缓存连接错误";
    public static final String JDBC_CONNECTION_ERROR = "数据库连接错误";
    public static final String UNCATCH_ERROR = "未捕捉的错误";
    public static final String ARGV_ERROR = "非法参数";
    public static final String UNKNOWN_ERROR ="未知错误";
    public static final String CHAIN_CONFIG_EXCEPTION ="责任链配置错误";
    public static final String UNCACHE_USERID = "未获取到用户id";
    public static final Object FREQUENT_OPERATION = "操作频繁";
    public static final Integer DEFAULT_PAGE = 1;
    public static final Integer DEFAULT_PAGESIZE = 10;


    public static final Long PASS_EXPIRE = 10L;
}
