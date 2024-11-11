package com.oasisnourish.dao.impl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.models.AuthToken;
import com.oasisnourish.models.JsonWebToken;
import com.oasisnourish.models.Token;

import redis.clients.jedis.JedisPooled;

@ExtendWith(MockitoExtension.class)
public class TokenDaoImplTest {

    @Mock
    private RedisConnection redisConnection;

    @Mock
    private JedisPooled jedis;

    @InjectMocks
    private TokenDaoImpl tokenDao;

    @BeforeEach
    public void setUp() {
        lenient().when(redisConnection.getJedis()).thenReturn(jedis);
    }

    @Test
    public void testSaveToken() {
        AuthToken authToken = new AuthToken("testToken", "AUTH", 1L, System.currentTimeMillis() + 60000, 1);
        String key = "user:" + authToken.getUserId() + ":token:" + authToken.getToken();

        tokenDao.saveToken(authToken);

        verify(jedis).hset(key, "tokenCategory", "auth");
        verify(jedis).hset(key, "tokenType", "AUTH");
        verify(jedis).hset(key, "tokenVersion", "1");
        verify(jedis).hset(key, "expires", String.valueOf(authToken.getExpires()));

        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);
        verify(jedis).expire(eq(key), ttlCaptor.capture());
        long ttlValue = ttlCaptor.getValue();
        assertTrue(ttlValue >= 59 && ttlValue <= 60, "Expected TTL to be within 59 to 60 seconds, but got " + ttlValue);
    }

    @Test
    void testSaveToken_TokenExpired() {
        AuthToken authToken = new AuthToken("expiredToken", "AUTH", 1L, System.currentTimeMillis() - 1000, 1);

        tokenDao.saveToken(authToken);

        verify(jedis, never()).hset(anyString(), anyString(), anyString());
        verify(jedis, never()).expire(anyString(), anyInt());
    }

    @Test
    void testFindToken_AuthTokenExists() {
        String token = "testToken";
        int userId = 1;
        String key = "user:" + userId + ":token:" + token;

        AuthToken expectedToken = new AuthToken(token, "confirmation", 1, 60000L, userId);
        when(jedis.exists(key)).thenReturn(true);
        when(jedis.hget(key, "tokenCategory")).thenReturn(expectedToken.getTokenCategory());
        when(jedis.hget(key, "tokenType")).thenReturn(expectedToken.getTokenType());
        when(jedis.hget(key, "tokenVersion")).thenReturn(String.valueOf(expectedToken.getTokenVersion()));
        when(jedis.hget(key, "expires")).thenReturn(String.valueOf(expectedToken.getExpires()));

        Optional<Token> result = tokenDao.findToken(token, userId);

        assertTrue(result.isPresent());
        assertEquals(AuthToken.class, result.get().getClass());
        AuthToken actualToken = (AuthToken) result.get();
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void testFindToken_JWTExists() {
        String token = "testToken";
        int userId = 1;
        String key = "user:" + userId + ":token:" + token;

        JsonWebToken expectedToken = new JsonWebToken(token, "refresh", 1, 60000L, userId);
        when(jedis.exists(key)).thenReturn(true);
        when(jedis.hget(key, "tokenCategory")).thenReturn(expectedToken.getTokenCategory());
        when(jedis.hget(key, "tokenType")).thenReturn(expectedToken.getTokenType());
        when(jedis.hget(key, "tokenVersion")).thenReturn(String.valueOf(expectedToken.getTokenVersion()));
        when(jedis.hget(key, "expires")).thenReturn(String.valueOf(expectedToken.getExpires()));

        Optional<Token> result = tokenDao.findToken(token, userId);

        assertTrue(result.isPresent());
        assertEquals(JsonWebToken.class, result.get().getClass());
        JsonWebToken actualToken = (JsonWebToken) result.get();
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void testFindToken_TokenDoesNotExist() {
        String token = "nonexistentToken";
        int userId = 1;
        String key = "user:" + userId + ":token:" + token;

        when(jedis.exists(key)).thenReturn(false);

        Optional<Token> result = tokenDao.findToken(token, userId);

        assertTrue(result.isEmpty());
        verify(jedis, never()).hget(anyString(), anyString());
    }

    @Test
    void testDeleteToken() {
        String token = "deleteToken";
        int userId = 1;
        String key = "user:" + userId + ":token:" + token;

        tokenDao.deleteToken(token, userId);

        verify(jedis).del(key);
    }
}
