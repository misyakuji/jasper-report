package com.misyakuji.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Primary
@Profile("!test")
public class RedisTokenBlacklist implements TokenBlacklist {

    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    private final StringRedisTemplate redisTemplate;
    private final int jwtExpirationMs;

    public RedisTokenBlacklist(StringRedisTemplate redisTemplate,
                               @Value("${app.jwtExpirationMs}") int jwtExpirationMs) {
        this.redisTemplate = redisTemplate;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    @Override
    public void blacklistToken(String token) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "1", jwtExpirationMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void blacklistToken(String token, long ttlMillis) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "1", ttlMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
