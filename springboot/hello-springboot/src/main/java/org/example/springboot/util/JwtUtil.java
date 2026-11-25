package org.example.springboot.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 签发与校验。
 *
 * 一个 token 三段：header.payload.signature，用 . 连接，每段都是 Base64URL。
 * - payload 是明文可解（所以绝不能放密码），只放 userId / username / role 这类"身份标签"
 * - signature = HMAC-SHA256(header + "." + payload, 密钥)，密钥只有服务器知道 → 改不了
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expireMillis;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expire-minutes}") long expireMinutes) {
        // HS256 要求密钥至少 256 位 = 32 字节，不够会直接抛异常（这是 jjwt 的保护）
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMillis = expireMinutes * 60_000L;
    }

    public String createToken(int userId, String username, int role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))   // sub：标准字段，放用户 id
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)                      // iat
                .expiration(new Date(now.getTime() + expireMillis))  // exp
                .signWith(key)
                .compact();
    }

    /**
     * 校验并解析。签名不对或已过期都会抛异常（JwtException / ExpiredJwtException），
     * 由调用方决定怎么处理 —— 我们统一转成 401。
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpireSeconds() {
        return expireMillis / 1000;
    }
}
