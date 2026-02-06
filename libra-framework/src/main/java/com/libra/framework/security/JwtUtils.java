package com.libra.framework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Removed @PostConstruct due to environments where annotation not available
import javax.crypto.SecretKey;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.security.SecureRandom;

@Component
public class JwtUtils {

    private static final int KEY_LENGTH = 32; // 256 bits

    @Value("${jwt.secret:libra-bms-secret-key-libra-bms-secret-key-libra-bms}")
    private String secret;

    @Value("${jwt.expiration:7200}")
    private long expiration;

    @Value("${jwt.refreshExpiration:604800}")
    private long refreshExpiration;

    private void ensureSecret() {
        if (secret == null || secret.trim().isEmpty()) {
            byte[] key = new byte[KEY_LENGTH];
            new SecureRandom().nextBytes(key);
            secret = Base64.getEncoder().encodeToString(key);
        } else {
            try {
                Base64.getDecoder().decode(secret);
            } catch (IllegalArgumentException ex) {
                secret = Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private SecretKey getSecretKey() {
        ensureSecret();
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(secret);
        } catch (Exception ex) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Long userId, Long tenantId, String username) {
        return createToken(userId, tenantId, username, expiration);
    }

    public String createRefreshToken(Long userId, Long tenantId, String username) {
        return createToken(userId, tenantId, username, refreshExpiration);
    }

    private String createToken(Long userId, Long tenantId, String username, long ttl) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("tenantId", tenantId);
        claims.put("username", username);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ttl * 1000))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
