package com.dylan.farmhouse.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类。
 */
public final class JwtUtil {

    // TODO: 生产环境密钥应从 Nacos 配置中心下发，切勿硬编码
    private static final String SECRET = "farmhouse-jwt-secret-key-please-change-me-2025-0123456789";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /** Token 有效期：24 小时 */
    private static final long EXPIRE_MILLIS = 24 * 60 * 60 * 1000L;

    private JwtUtil() {
    }

    public static String generateToken(Long userId, String username, Integer role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + EXPIRE_MILLIS))
                .signWith(KEY)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static Long getUserId(Claims claims) {
        Object value = claims.get("userId");
        return value instanceof Number n ? n.longValue() : Long.valueOf(value.toString());
    }

    public static Integer getRole(Claims claims) {
        Object value = claims.get("role");
        return value instanceof Number n ? n.intValue() : Integer.valueOf(value.toString());
    }

    public static String getUsername(Claims claims) {
        return claims.getSubject();
    }
}
