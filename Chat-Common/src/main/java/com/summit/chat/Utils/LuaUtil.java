package com.summit.chat.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class LuaUtil {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public <T> T execute(List<Object> key, String path, Class<T> resultType, Object... args) {
        DefaultRedisScript<T> objectDefaultRedisScript = new DefaultRedisScript<>();
        objectDefaultRedisScript.setLocation(new ClassPathResource(path));
        objectDefaultRedisScript.setResultType(resultType);
        log.info("【Lua工具】lua脚本{}已启动", path);

        List<String> stringKey = key.stream().map(String::valueOf).toList();

        Object[] stringArgs = Arrays.stream(args).map(String::valueOf).toArray();
        return redisTemplate.execute(objectDefaultRedisScript, stringKey, stringArgs);
    }
}
