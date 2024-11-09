package com.oasisnourish.dao.impl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.models.AuthToken;

import redis.clients.jedis.JedisPooled;


public class AuthTokenDaoImplTest {
    
    @Mock
    private RedisConnection redisConnection;

    @Mock
    private JedisPooled jedisPooled;

    @InjectMocks
    private AuthTokenDaoImpl authTokenDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisConnection.getJedis()).thenReturn(jedisPooled);
    }

    @Test
    public void testSaveToken_withValidToken_savesToRedis() {
        // Token should expire in about 60 seconds
        AuthToken token = new AuthToken("validToken", 123, System.currentTimeMillis() + 60000, "AUTH");
        
        authTokenDao.saveToken(token);

        String key = "jwt:" + token.getToken();
        verify(jedisPooled).hset(key, "userId", String.valueOf(token.getUserId()));
        verify(jedisPooled).hset(key, "expires", String.valueOf(token.getExpires()));
        verify(jedisPooled).hset(key, "tokenType", token.getTokenType());

        // Capture the TTL argument and check it falls within a 1-second range
        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);
        verify(jedisPooled).expire(eq(key), ttlCaptor.capture());
        long ttlValue = ttlCaptor.getValue();
        assertTrue(ttlValue >= 59 && ttlValue <= 60, "Expected TTL to be within 59 to 60 seconds, but got " + ttlValue);
    }

    @Test
    public void testSaveToken_withExpiredToken_doesNotSave() {
        AuthToken expiredToken = new AuthToken("expiredToken", 123, System.currentTimeMillis() - 1000, "AUTH");
        
        authTokenDao.saveToken(expiredToken);

        verify(jedisPooled, never()).hset(anyString(), anyString(), anyString());
        verify(jedisPooled, never()).expire(anyString(), anyInt());
    }

    @Test
    public void testFindToken_withExistingToken_returnsToken() {
        String tokenKey = "auth-token:existingToken";
        when(jedisPooled.exists(tokenKey)).thenReturn(true);
        when(jedisPooled.hget(tokenKey, "userId")).thenReturn("123");
        when(jedisPooled.hget(tokenKey, "expires")).thenReturn(String.valueOf(System.currentTimeMillis() + 60000));
        when(jedisPooled.hget(tokenKey, "tokenType")).thenReturn("AUTH");

        Optional<AuthToken> result = authTokenDao.findToken("existingToken");

        assertTrue(result.isPresent());
        AuthToken token = result.get();
        assertEquals("existingToken", token.getToken());
        assertEquals(123, token.getUserId());
        assertEquals("AUTH", token.getTokenType());
    }

    @Test
    public void testFindToken_withNonExistingToken_returnsEmpty() {
        when(jedisPooled.exists("auth-token:nonExistingToken")).thenReturn(false);

        Optional<AuthToken> result = authTokenDao.findToken("nonExistingToken");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteToken_deletesTokenFromRedis() {
        authTokenDao.deleteToken("toBeDeletedToken");

        verify(jedisPooled).del("auth-token:toBeDeletedToken");
    }
}
