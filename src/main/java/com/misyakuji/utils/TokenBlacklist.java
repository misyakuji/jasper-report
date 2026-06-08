package com.misyakuji.utils;

public interface TokenBlacklist {
    void blacklistToken(String token);
    void blacklistToken(String token, long ttlMillis);
    boolean isBlacklisted(String token);
}
