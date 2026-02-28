package com.summit.chat.Constants;

public class EmojiConstants {
    public static final String CURSOR_ERROR = "游标配置错误";
    public static final String EMOJI_NO_EXIST = "表情不存在或状态已经改变";
    public static final String ILLEGAL_CHAR = "非法字符";
    public static final String URL_NULL = "表情未上传";
    public static final String CATEGORY_NOT_EXIST = "不存在分类";
    public static final String CONTENT_NULL = "未输入内容";
    public static final String FILE_PROCESSING_ERROR = "表情上传失败";
    public static final String FILE_READ_ERROR = "表情读取错误";
    public static final int DEFAULT_EMOJI_MAX_WIDTH = 1000;
    public static final int DEFAULT_EMOJI_MAX_HEIGHT = 1000;
    public static final String INVALID_IMAGE_DIMENSIONS = String.format("图片尺寸错误，宽高不能超过%d*%d", DEFAULT_EMOJI_MAX_WIDTH, DEFAULT_EMOJI_MAX_HEIGHT);
}
