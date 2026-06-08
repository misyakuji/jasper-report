package com.misyakuji.utils;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("test")
public class InMemoryTokenBlacklist implements TokenBlacklist {

    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    @Override
    public void blacklistToken(String token) {
        blacklist.add(token);
    }

    @Override
    public void blacklistToken(String token, long ttlMillis) {
        blacklist.add(token);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
