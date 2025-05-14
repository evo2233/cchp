package org.example.demo.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    // 建议密钥长度 >= 32 字节（HMAC SHA-256 要求）
    private static final String JWT_SECRET = "cchp_secret_key_should_be_very_long_and_secure_123456";
    private static final long JWT_EXPIRATION = 12 * 3600 * 1000; // 12小时

    // 创建符合 HMAC SHA256 要求的 SecretKey
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            JWT_SECRET.getBytes(StandardCharsets.UTF_8)
    );

    /**
     * 生成 JWT Token
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION)) // 过期时间
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 使用 HMAC-SHA256 签名
                .compact();
    }

    /**
     * 解析 Token，返回 Claims
     */
    public static Claims parseToken(String token) throws Exception {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) // 验签
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
