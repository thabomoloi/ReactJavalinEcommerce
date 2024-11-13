package com.oasisnourish.dao.impl;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        AuthToken authToken = new AuthToken("testToken", "AUTH", 1L, Instant.now().plusSeconds(60L), 1);
        String key = "token:" + authToken.getToken();

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
        AuthToken authToken = new AuthToken("expiredToken", "AUTH", 1L, Instant.now().minusSeconds(10L), 1);

        tokenDao.saveToken(authToken);

        verify(jedis, never()).hset(anyString(), anyString(), anyString());
        verify(jedis, never()).expire(anyString(), anyInt());
    }

    @Test
    void testFindToken_AuthTokenExists() {
        String token = "testToken";
        int userId = 1;
        String key = "token:" + token;

        AuthToken expectedToken = new AuthToken(token, "confirmation", 1, Instant.now().plusSeconds(60L), userId);
        when(jedis.exists(key)).thenReturn(true);
        when(jedis.hget(key, "tokenCategory")).thenReturn(expectedToken.getTokenCategory());
        when(jedis.hget(key, "tokenType")).thenReturn(expectedToken.getTokenType());
        when(jedis.hget(key, "tokenVersion")).thenReturn(String.valueOf(expectedToken.getTokenVersion()));
        when(jedis.hget(key, "expires")).thenReturn(String.valueOf(expectedToken.getExpires()));
        when(jedis.hget(key, "userId")).thenReturn(String.valueOf(expectedToken.getUserId()));

        Optional<Token> result = tokenDao.findToken(token);

        assertTrue(result.isPresent());
        assertEquals(AuthToken.class, result.get().getClass());
        AuthToken actualToken = (AuthToken) result.get();
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void testFindToken_JWTExists() {
        String token = "testToken";
        int userId = 1;
        String key = "token:" + token;

        JsonWebToken expectedToken = new JsonWebToken(token, "refresh", 1, Instant.now().plusSeconds(60L), userId);
        when(jedis.exists(key)).thenReturn(true);
        when(jedis.hget(key, "tokenCategory")).thenReturn(expectedToken.getTokenCategory());
        when(jedis.hget(key, "tokenType")).thenReturn(expectedToken.getTokenType());
        when(jedis.hget(key, "tokenVersion")).thenReturn(String.valueOf(expectedToken.getTokenVersion()));
        when(jedis.hget(key, "expires")).thenReturn(String.valueOf(expectedToken.getExpires()));
        when(jedis.hget(key, "userId")).thenReturn(String.valueOf(expectedToken.getUserId()));

        Optional<Token> result = tokenDao.findToken(token);

        assertTrue(result.isPresent());
        assertEquals(JsonWebToken.class, result.get().getClass());
        JsonWebToken actualToken = (JsonWebToken) result.get();
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void testFindToken_TokenDoesNotExist() {
        String token = "nonexistentToken";
        String key = "token:" + token;

        when(jedis.exists(key)).thenReturn(false);

        Optional<Token> result = tokenDao.findToken(token);

        assertTrue(result.isEmpty());
        verify(jedis, never()).hget(anyString(), anyString());
    }

    @Test
    void testDeleteToken() {
        String token = "deleteToken";
        String key = "token:" + token;

        tokenDao.deleteToken(token);

        verify(jedis).del(key);
    }

    @Test
    public void testFindAllTokens() {
        int userId = 1;
        String pattern = "token:*";
        String tokenKey1 = "auth-token1";
        String tokenKey2 = "auth-token2";
        Instant expires1 = Instant.now().plusSeconds(60L);
        Instant expires2 = Instant.now().plusSeconds(120L);

        Set<String> keys = new HashSet<>();
        keys.add(tokenKey1);
        keys.add(tokenKey2);

        when(jedis.keys(pattern)).thenReturn(keys);
        when(jedis.hget(tokenKey1, "tokenType")).thenReturn("type1");
        when(jedis.hget(tokenKey1, "tokenVersion")).thenReturn("1");
        when(jedis.hget(tokenKey1, "expires")).thenReturn(String.valueOf(expires1));
        when(jedis.hget(tokenKey1, "userId")).thenReturn("1");
        when(jedis.hget(tokenKey1, "tokenCategory")).thenReturn("auth");

        when(jedis.hget(tokenKey2, "tokenType")).thenReturn("type2");
        when(jedis.hget(tokenKey2, "tokenVersion")).thenReturn("2");
        when(jedis.hget(tokenKey2, "expires")).thenReturn(String.valueOf(expires2));
        when(jedis.hget(tokenKey2, "userId")).thenReturn("1");
        when(jedis.hget(tokenKey2, "tokenCategory")).thenReturn("auth");

        List<Token> tokens = tokenDao.findTokensByUserId(userId);

        assertEquals(2, tokens.size());

        Token token1 = tokens.get(0);
        assertEquals(tokenKey1, token1.getToken());
        assertEquals("type1", token1.getTokenType());
        assertEquals(1L, token1.getTokenVersion());
        assertEquals(expires1, token1.getExpires());
        assertEquals(1, token1.getUserId());

        Token token2 = tokens.get(1);
        assertEquals(tokenKey2, token2.getToken());
        assertEquals("type2", token2.getTokenType());
        assertEquals(2L, token2.getTokenVersion());
        assertEquals(expires2, token2.getExpires());
        assertEquals(1, token2.getUserId());
    }
}
