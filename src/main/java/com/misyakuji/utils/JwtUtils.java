package com.misyakuji.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtils {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /** 自动续期阈值（10 分钟）。 */
    private static final long REFRESH_THRESHOLD = 10 * 60 * 1000L;

    /**
     * 获取签名密钥。
     *
     * @return 签名密钥
     */
    private SecretKey signingKey() {
//        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
//        SecretKey key = Keys.hmacShaKeyFor(HexFormat.of().parseHex(jwtSecret));
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT。
     *
     * @param username 用户名
     * @param role 角色
     * @return JWT 字符串
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
//        String token = Jwts.builder()
//                .claim(Claims.ID, UUID.randomUUID().toString())
//                .claim(Claims.SUBJECT, username)
//                .claim("role", role)
//                .claim(Claims.ISSUER, "jasper-report")
//                .claim(Claims.AUDIENCE, "jasper-report-api")
//                .claim(Claims.ISSUED_AT, now)
//                .claim(Claims.EXPIRATION, expiryDate)
//                .signWith(signingKey())
//                .compact();
        /*
          jti: JWT 的唯一标识符，用于防止重放攻击
          sub: 主题，通常为用户标识（用户名）
          claim: 自定义声明,此处用于存放用户角色/权限信息，便于授权判断
          iss: 签发者标识，用于验证 token 来源
          aud: 受众，指明此 token 预期使用的目标服务或资源
          iat: 签发时间，表示 token 何时被创建
          exp: 到期时间，超过该时间后 token 视为失效
          使用 SecretKey 对 token 进行签名
         */
        String token= Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .claim("role", role)
                .issuer("jasper-report")
                .audience().add("jasper-report-api").and()
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey())
                .compact();

        log.debug("Generated JWT: {}", token);
        log.info("JWT generated successfully for user: {}, expires at: {}", username, expiryDate);
        return token;
    }

    /**
     * 解析并验证 JWT。
     *
     * @param token JWT 字符串
     * @return 解析结果
     */
    public Jws<Claims> parseJwsClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token);
    }

    /**
     * 解析 JWT 载荷。
     *
     * @param token JWT 字符串
     * @return Claims 载荷
     */
    public Claims parseClaims(String token) {
        return parseJwsClaims(token).getPayload();
    }

    /**
     * 兼容旧接口，返回解析结果。
     *
     * @param token JWT 字符串
     * @return 解析结果
     */
    public Jws<Claims> parseToken(String token) {
        return parseJwsClaims(token);
    }

    /**
     * 校验 token 是否有效。
     *
     * @param token JWT 字符串
     * @return 有效返回 {@code true}，否则返回 {@code false}
     */
    public boolean validateToken(String token) {
        try {
            parseJwsClaims(token);
            return true;
        } catch (JwtException ex) {
            log.debug("JWT validation failed", ex);
            return false;
        }
    }

    /**
     * 从 token 中获取用户名。
     *
     * @param token JWT 字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 判断 token 是否接近过期。
     *
     * @param token JWT 字符串
     * @return 接近过期返回 {@code true}，否则返回 {@code false}
     */
    public boolean shouldRefresh(String token) {
        long remaining = parseClaims(token).getExpiration().getTime() - System.currentTimeMillis();
        return remaining < REFRESH_THRESHOLD;
    }

    /**
     * 判断是否接近过期。
     *
     * @param expiration 过期时间
     * @return 接近过期返回 {@code true}，否则返回 {@code false}
     */
    public boolean shouldRefresh(Date expiration) {
        long remaining = expiration.getTime() - System.currentTimeMillis();
        return remaining < REFRESH_THRESHOLD;
    }
}