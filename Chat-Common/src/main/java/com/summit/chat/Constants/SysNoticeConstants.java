package com.summit.chat.Constants;

/**
 * 系统公告常量类
 */
public class SysNoticeConstants {
    
    /**
     * 正常状态
     */
    public static final Integer STATUS_NORMAL = 0;
    
    /**
     * 删除状态
     */
    public static final Integer STATUS_DELETED = 1;

    /**
     * 异常提示信息
     */
    public static final String NOTICE_ID_NULL = "公告ID不能为空";
    public static final String NOTICE_MSG_NULL = "公告内容不能为空";
    public static final String NOTICE_NOT_EXIST = "公告不存在或已被删除";
    public static final String PUBLISHER_ID_NULL = "发布者ID不能为空";
    
    /**
     * 缓存前缀
     */
    public static final String NOTICE_CACHE_PREFIX = "sys:notice:";
    public static final String NOTICE_CACHE_LIST = "sys:notice:list";
}
