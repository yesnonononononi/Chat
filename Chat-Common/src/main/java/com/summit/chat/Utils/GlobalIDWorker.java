package com.summit.chat.Utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;


public class GlobalIDWorker {

    public static  String generateToken(){

        UUID uuid = UUID.randomUUID();
        String token = uuid+"";

        return token;
    }

    /**
     * 雪花算法的id生成器
     */
    public static String generateId(){
        Snowflake snowflake = IdUtil.getSnowflake();
        long UserID = snowflake.nextId();
        return String.valueOf(UserID);
    }
}
