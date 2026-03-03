package com.pomodoro.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        jwtUtil.setSecret("testSecretKeyThatIsAtLeast256BitsLongForHmacSha256Algorithm!");
        jwtUtil.setAccessTokenExpiration(900000L);
        jwtUtil.setRefreshTokenExpiration(604800000L);
    }

    @Test
    void generateAndValidateAccessToken() {
        String token = jwtUtil.generateAccessToken(1L, "testuser");

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertTrue(jwtUtil.isAccessToken(token));
        assertFalse(jwtUtil.isRefreshToken(token));
        assertEquals(1L, jwtUtil.getUserIdFromToken(token));
        assertEquals("testuser", jwtUtil.getUsernameFromToken(token));
    }

    @Test
    void generateAndValidateRefreshToken() {
        String token = jwtUtil.generateRefreshToken(42L, "alice");

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertTrue(jwtUtil.isRefreshToken(token));
        assertFalse(jwtUtil.isAccessToken(token));
        assertEquals(42L, jwtUtil.getUserIdFromToken(token));
        assertEquals("alice", jwtUtil.getUsernameFromToken(token));
    }

    @Test
    void invalidTokenReturnsFalse() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }
}
