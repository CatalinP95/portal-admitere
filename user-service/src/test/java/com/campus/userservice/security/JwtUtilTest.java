package com.campus.userservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", 604800000L);
    }

    @Test
    void generateToken_isValid() {
        String token = jwtUtil.generateToken("42", "STUDENT");
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void extractUserId_returnsCorrectId() {
        String token = jwtUtil.generateToken("42", "STUDENT");
        assertEquals("42", jwtUtil.extractUserId(token));
    }

    @Test
    void extractRole_returnsCorrectRole() {
        String token = jwtUtil.generateToken("42", "ADMIN");
        assertEquals("ADMIN", jwtUtil.extractRole(token));
    }

    @Test
    void generateRefreshToken_isValid() {
        String token = jwtUtil.generateRefreshToken("1", "STUDENT");
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void invalidToken_returnsFalse() {
        assertFalse(jwtUtil.isTokenValid("not.a.valid.token"));
    }

    @Test
    void emptyToken_returnsFalse() {
        assertFalse(jwtUtil.isTokenValid(""));
    }
}
