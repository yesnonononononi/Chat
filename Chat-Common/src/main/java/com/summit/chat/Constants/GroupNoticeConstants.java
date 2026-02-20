package com.summit.chat.Constants;

/**
 * 群公告常量类
 */
public class GroupNoticeConstants {
    
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
    public static final String GROUP_ID_NULL = "群组ID不能为空";
    public static final String PUBLISHER_ID_NULL = "发布者ID不能为空";
    public static final String NOTICE_CONTENT_NULL = "公告内容不能为空";
    public static final String NOTICE_ID_NULL = "公告ID不能为空";
    public static final String NOTICE_NOT_EXIST = "公告不存在或已被删除";
    public static final String NOTICE_ID_LIST_NULL = "公告ID列表不能为空";
    public static final String NOTICE_CACHE_PREFIX = "group:notice:";
    public static final String NOTICE_CACHE_PREFIX_LIST = "group:notice:list:";
    public static final int DEFAULT_MAX_LENGTH = 200;
    public static final String NOTICE_CONTENT_TOO_LONG = "群公告内容过多";
}
