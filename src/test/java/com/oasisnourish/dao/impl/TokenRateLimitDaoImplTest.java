package com.oasisnourish.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.models.AuthToken;

import redis.clients.jedis.JedisPooled;

@ExtendWith(MockitoExtension.class)
public class TokenRateLimitDaoImplTest {

    @Mock
    private RedisConnection redisConnection;

    @Mock
    private JedisPooled jedis;

    @InjectMocks
    private TokenRateLimitDaoImpl tokenRateLimitDao;

    @BeforeEach
    public void setUp() {
        when(redisConnection.getJedis()).thenReturn(jedis);
    }

    @Test
    public void testFind_KeyExists() {
        int userId = 1;
        String key = "user:" + userId + ":token-rate-limit";

        when(jedis.exists(key)).thenReturn(true);
        when(jedis.get(key)).thenReturn("3");

        long result = tokenRateLimitDao.find(userId);

        assertEquals(3, result);
        verify(jedis).exists(key);
        verify(jedis).get(key);
    }

    @Test
    public void testFind_KeyDoesNotExist() {
        int userId = 1;
        String key = "user:" + userId + ":token-rate-limit";

        when(jedis.exists(key)).thenReturn(false);
        when(jedis.get(key)).thenReturn("1");

        long result = tokenRateLimitDao.find(userId);

        assertEquals(1, result);
        verify(jedis).exists(key);
        verify(jedis).set(key, "1");
        verify(jedis).get(key);
    }

    @Test
    public void testIncrement_KeyExists() {
        int userId = 1;
        int expires = 300;
        String key = "user:" + userId + ":token-rate-limit";

        when(jedis.exists(key)).thenReturn(true);
        when(jedis.incr(key)).thenReturn(4L);

        long result = tokenRateLimitDao.increment(userId, expires);

        assertEquals(4, result);
        verify(jedis).exists(key);
        verify(jedis).expire(key, expires);
        verify(jedis).incr(key);
    }

    @Test
    public void testIncrement_KeyDoesNotExist() {
        int userId = 1;
        int expires = 300;
        String key = "user:" + userId + ":token-rate-limit";

        when(jedis.exists(key)).thenReturn(false);
        when(jedis.incr(key)).thenReturn(1L);

        long result = tokenRateLimitDao.increment(userId, expires);

        assertEquals(1, result);
        verify(jedis).exists(key);
        verify(jedis).set(key, "0");
        verify(jedis).expire(key, expires);
        verify(jedis).incr(key);
    }

    @Test
    public void testReset() {
        int userId = 1;
        String key = "user:" + userId + ":token-rate-limit";

        tokenRateLimitDao.reset(userId);

        verify(jedis).del(key);
    }

    @Test
    public void testFindAllTokens() {
        int userId = 1;
        String pattern = "user:" + userId + ":token:*";
        String tokenKey1 = "user:1:token:auth-token1";
        String tokenKey2 = "user:1:token:auth-token2";

        Set<String> keys = new HashSet<>();
        keys.add(tokenKey1);
        keys.add(tokenKey2);

        when(jedis.keys(pattern)).thenReturn(keys);
        when(jedis.hget(tokenKey1, "tokenType")).thenReturn("type1");
        when(jedis.hget(tokenKey1, "tokenVersion")).thenReturn("1");
        when(jedis.hget(tokenKey1, "expires")).thenReturn("600");
        when(jedis.hget(tokenKey1, "userId")).thenReturn("1");

        when(jedis.hget(tokenKey2, "tokenType")).thenReturn("type2");
        when(jedis.hget(tokenKey2, "tokenVersion")).thenReturn("2");
        when(jedis.hget(tokenKey2, "expires")).thenReturn("1200");
        when(jedis.hget(tokenKey2, "userId")).thenReturn("1");

        List<AuthToken> tokens = tokenRateLimitDao.findAllTokens(userId);

        assertEquals(2, tokens.size());

        AuthToken token1 = tokens.get(0);
        assertEquals(tokenKey1, token1.getToken());
        assertEquals("type1", token1.getTokenType());
        assertEquals(1L, token1.getTokenVersion());
        assertEquals(600L, token1.getExpires());
        assertEquals(1, token1.getUserId());

        AuthToken token2 = tokens.get(1);
        assertEquals(tokenKey2, token2.getToken());
        assertEquals("type2", token2.getTokenType());
        assertEquals(2L, token2.getTokenVersion());
        assertEquals(1200L, token2.getExpires());
        assertEquals(1, token2.getUserId());
    }
}
