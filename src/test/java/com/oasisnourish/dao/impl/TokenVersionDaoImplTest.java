package com.oasisnourish.dao.impl;

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

import redis.clients.jedis.JedisPooled;

@ExtendWith(MockitoExtension.class)
public class TokenVersionDaoImplTest {

    @Mock
    private RedisConnection redisConnection;

    @Mock
    private JedisPooled jedis;

    @InjectMocks
    private TokenVersionDaoImpl tokenVersionDao;

    @BeforeEach
    public void setUp() {
        when(redisConnection.getJedis()).thenReturn(jedis);
    }

    @Test
    public void testFind_KeyExists() {
        int userId = 1;
        String tokenCategory = "testCategory";
        String tokenType = "testType";
        String key = "user:" + userId + ":token-category:" + tokenCategory + ":token-type:" + tokenType;

        when(jedis.exists(key)).thenReturn(true);
        when(jedis.get(key)).thenReturn("3");

        long version = tokenVersionDao.find(userId, tokenCategory, tokenType);
        assertEquals(3, version);
        verify(jedis).exists(key);
        verify(jedis).get(key);
    }

    @Test
    void testFind_KeyDoesNotExist() {
        int userId = 1;
        String tokenCategory = "testCategory";
        String tokenType = "testType";
        String key = "user:" + userId + ":token-category:" + tokenCategory + ":token-type:" + tokenType;

        when(jedis.exists(key)).thenReturn(false);
        when(jedis.get(key)).thenReturn("1");

        long version = tokenVersionDao.find(userId, tokenCategory, tokenType);

        assertEquals(1, version);
        verify(jedis).exists(key);
        verify(jedis).set(key, "1");
        verify(jedis).get(key);
    }

    @Test
    void testIncrement_KeyExists() {
        int userId = 1;
        String tokenCategory = "testCategory";
        String tokenType = "testType";
        String key = "user:" + userId + ":token-category:" + tokenCategory + ":token-type:" + tokenType;

        when(jedis.exists(key)).thenReturn(true);
        when(jedis.incr(key)).thenReturn(4L);

        long version = tokenVersionDao.increment(userId, tokenCategory, tokenType);

        assertEquals(4, version);
        verify(jedis).exists(key);
        verify(jedis).incr(key);
    }

    @Test
    void testIncrement_KeyDoesNotExist() {
        int userId = 1;
        String tokenCategory = "testCategory";
        String tokenType = "testType";
        String key = "user:" + userId + ":token-category:" + tokenCategory + ":token-type:" + tokenType;

        when(jedis.exists(key)).thenReturn(false);
        when(jedis.incr(key)).thenReturn(1L);

        long version = tokenVersionDao.increment(userId, tokenCategory, tokenType);

        assertEquals(1, version);
        verify(jedis).exists(key);
        verify(jedis).set(key, "0");
        verify(jedis).incr(key);
    }
}
