package com.summit.chat.service.Impl.Support;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.summit.chat.Mapper.Cache.CacheSupport;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TokenSupport {
    CacheSupport cacheSupport;

    public TokenSupport(CacheSupport cacheSupport) {
        this.cacheSupport = cacheSupport;
    }


    public Token builder() {
        return new Token();
    }

    public class Token {
        private String tokenPrefix;
        private Long duration;
        private TimeUnit timeUnit;

        public Token generate(String tokenPrefix) {
            if (StrUtil.isBlank(tokenPrefix)) {
                throw new IllegalArgumentException("非法参数");
            }
            this.tokenPrefix = tokenPrefix;
            return this;
        }

        public Token expire(Long duration, TimeUnit timeUnit) {
            if (duration < 0L || timeUnit == null) {
                throw new IllegalArgumentException("存在时间不能小于0,确保参数完整");
            }
            this.duration = duration;
            this.timeUnit = timeUnit;
            return this;
        }

        public String build() {


            if (tokenPrefix == null ) {
                throw new IllegalArgumentException("请先构建必要步骤generate&expire");
            }
            String token =  generateSalt();

            return token;
        }

    }


    private String buildKey(String tokenPrefix, String key) {
        return tokenPrefix + key;
    }

    private String generateSalt() {
        return UUID.randomUUID().toString();
    }

}
