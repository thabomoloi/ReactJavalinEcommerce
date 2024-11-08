package com.oasisnourish.services.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.exceptions.InvalidTokenException;
import com.oasisnourish.exceptions.TooManyRequestsException;

import redis.clients.jedis.JedisPooled;

public class TokenServiceImplTest {
    @Mock
    private RedisConnection redisConnection;

    @Mock
    private JedisPooled jedis;

    @InjectMocks
    private TokenServiceImpl tokenService;

    private final int userId = 1;
    private final String tokenType = "email_verification";
    private final String tokenKey = "tokens:" + userId + ":" + tokenType;
    private final String rateLimitKey = "rate_limit:" + userId + ":" + tokenType;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisConnection.getJedis()).thenReturn(jedis);
    }

    @Test
    void generateToken_withinRateLimit_generatesToken() throws TooManyRequestsException {
        when(jedis.get(rateLimitKey)).thenReturn(null);
        when(jedis.get(tokenKey)).thenReturn(null);

        String token = tokenService.generateToken(userId, tokenType);

        assertNotNull(token);
        verify(jedis).del(tokenKey);
        verify(jedis).setex(eq(tokenKey), eq(30l * 60), eq(token));
        verify(jedis).incr(rateLimitKey);
        verify(jedis).expire(rateLimitKey, 24 * 60 * 60);
    }

    @Test
    void generateToken_exceedsRateLimit_throwsTooManyRequestsException() {
        when(jedis.get(rateLimitKey)).thenReturn("3");

        assertThrows(TooManyRequestsException.class, () -> {
            tokenService.generateToken(userId, tokenType);
        });
    }

    @Test
    void verifyToken_withValidToken_returnsTrue() {
        String token = "test-token";
        when(jedis.get(tokenKey)).thenReturn(token);

        boolean isValid = tokenService.verifyToken(userId, tokenType, token);

        assertTrue(isValid);
    }

    @Test
    void verifyToken_withInvalidToken_returnsFalse() {
        String token = "test-token";
        when(jedis.get(tokenKey)).thenReturn("different-token");

        boolean isValid = tokenService.verifyToken(userId, tokenType, token);

        assertFalse(isValid);
    }

    @Test
    void revokeToken_removesTokenFromRedis() {
        tokenService.revokeToken(userId, tokenType);

        verify(jedis).del(tokenKey);
    }

    @Test
    void verifyTokenOrThrow_withValidToken_doesNotThrow() {
        String token = "test-token";
        when(jedis.get(tokenKey)).thenReturn(token);

        assertDoesNotThrow(() -> tokenService.verifyTokenOrThrow(userId, tokenType, token));
    }

    @Test
    void verifyTokenOrThrow_withInvalidToken_throwsInvalidTokenException() {
        String token = "test-token";
        when(jedis.get(tokenKey)).thenReturn("different-token");

        assertThrows(InvalidTokenException.class, () -> {
            tokenService.verifyTokenOrThrow(userId, tokenType, token);
        });
    }
}