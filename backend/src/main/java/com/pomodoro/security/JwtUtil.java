package com.pomodoro.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Setter
public class JwtUtil {

    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public String generateAccessToken(Long userId, String username) {
        return buildToken(userId, username, accessTokenExpiration, TOKEN_TYPE_ACCESS);
    }

    public String generateRefreshToken(Long userId, String username) {
        return buildToken(userId, username, refreshTokenExpiration, TOKEN_TYPE_REFRESH);
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        Claims claims = parseToken(token);
        return TOKEN_TYPE_ACCESS.equals(claims.get("type", String.class));
    }

    public boolean isRefreshToken(String token) {
        Claims claims = parseToken(token);
        return TOKEN_TYPE_REFRESH.equals(claims.get("type", String.class));
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    private String buildToken(Long userId, String username, long expiration, String tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("type", tokenType)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
