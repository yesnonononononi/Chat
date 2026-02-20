package com.summit.chat.Constants;

public class ShakeConstants {
    public static final String CACHE_PREFIX_FOR_RESULT = "result:";
    public static final String CACHE_SET_ERROR = "缓存设置时发生异常";
    public static final String CACHE_GET_ERROR = "从缓存中取结果时发生异常";
    public static final String NULL_VAL_OF_ANNO = "从注解中取结果发生异常";
    public static final String UNKNOWN_ERROR_IN_REGISTRY = "注册参数时发生错误";
    public static final String SPEL_ERROR = "spel表达式错误,未找到参数";
    public static final String UNKNOWN_ERROR_IN_PARSE = "解析SPEL表达式时发生错误";
    public static final String SHAKE_LUA_RESULT_NONE = "NONE";
    public static final String SHAKE_LUA_RESULT_PROCESS = "PROCESS";
    public static final String SHAKE_LUA_RESULT_PATH = "Lua/ShakeProtect.lua";
    public static final String SHAKE_LUA_RESULT_PATH_1 = "Lua/ShakeProtect_1.lua";
    public static final String SHAKE_LUA_CACHE_PREFIX = "shake:result:id:";
    public static final String ARGV_ERROR = "未读取到幂等id";
    public static final String CACHE_EXPIRE_SECONDS = "10";
    /**
     * HOLD 占位符的过期时间（秒）。
     * <p>
     * 作用：防止业务线程异常/卡死导致占位符永不释放。
     * 注意：该值应当大于接口的最大预期执行时间，避免过早过期导致并发穿透。
     */
    public static final String SHAKE_HOLD_EXPIRE_SECONDS = "60";
    public static final String SHAKE_LUA_CLEAN_HOLD_PATH = "Lua/ShakeProtectClean.lua";
    public static final String REQUEST_REPEAT = "操作频繁,请稍后再试";
    public static final String DEFAULT_HOLD = "default_hold";
}
