package com.misyakuji.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // 10分钟自动续期阈值
    private static final long REFRESH_THRESHOLD = 10 * 60 * 1000L;

    /**
     * 生成 JWT Token（保持不变）
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(username)
                .claim("role", role)
                .setIssuer("jasper-report")
                .setAudience("jasper-report-api")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解析 Token：用旧版 parser() 方法（0.13.x 兼容）
     */
    public Claims parseToken(String token) {
        SecretKey key = signingKey();

        try {
            // 关键修改：用 parser() 替代 parserBuilder()，0.13.x 仍支持
            return Jwts.parser()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            throw ex;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
            throw ex;
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            throw ex;
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
            throw ex;
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
            throw ex;
        }
    }

    /**
     * 从 Token 获取用户名
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 验证 Token 有效性
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 判断是否需要续期
     */
    public boolean shouldRefresh(String token) {
        Claims claims = parseToken(token);
        long remaining = claims.getExpiration().getTime() - System.currentTimeMillis();
        return remaining < REFRESH_THRESHOLD;
    }
    public boolean shouldRefresh(Date expiration) {
        long remaining = expiration.getTime() - System.currentTimeMillis();
        return remaining < REFRESH_THRESHOLD;
    }
}