package com.summit.chat.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PaginationConstants {
    // 时间格式化器（前后端交互统一格式）
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    // 初始游标（倒序查询最新数据时用）：最大时间值
    public static final LocalDateTime INIT_CURSOR_DESC = LocalDateTime.of(9999, 12, 31, 23, 59, 59, 999_000_000);

    // 初始游标（正序查询最早数据时用）：最小时间值
    public static final LocalDateTime INIT_CURSOR_ASC = LocalDateTime.of(0, 1, 1, 0, 0, 0, 0);

    // 初始游标字符串（前端传参用）
    public static final String INIT_CURSOR_DESC_STR = INIT_CURSOR_DESC.format(DATETIME_FORMATTER);
    public static final String INIT_CURSOR_ASC_STR = INIT_CURSOR_ASC.format(DATETIME_FORMATTER);
}
