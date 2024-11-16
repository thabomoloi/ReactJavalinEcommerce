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
import com.oasisnourish.enums.Tokens;

import redis.clients.jedis.JedisPooled;

@ExtendWith(MockitoExtension.class)
public class TokenVersionDaoImplTest {

    @Mock
    private RedisConnection redisConnection;

    @Mock
    private JedisPooled jedis;

    @InjectMocks
    private TokenVersionDaoImpl tokenVersionDao;

    private final int userId = 1;
    private final Tokens.Category tokenCategory = Tokens.Category.AUTH;
    private final Tokens.Auth tokenType = Tokens.Auth.PASSWORD_RESET_TOKEN;
    private final String key = "user:" + userId + ":token-category:" + tokenCategory.getCategory() + ":token-type:" + tokenType.getType();

    @BeforeEach
    public void setUp() {
        when(redisConnection.getJedis()).thenReturn(jedis);
    }

    @Test
    public void testFind_KeyExists() {
        when(jedis.exists(key)).thenReturn(true);
        when(jedis.get(key)).thenReturn("3");

        long version = tokenVersionDao.find(userId, tokenCategory, tokenType);
        assertEquals(3, version);
        verify(jedis).exists(key);
        verify(jedis).get(key);
    }

    @Test
    void testFind_KeyDoesNotExist() {
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
        when(jedis.exists(key)).thenReturn(true);
        when(jedis.incr(key)).thenReturn(4L);

        long version = tokenVersionDao.increment(userId, tokenCategory, tokenType);

        assertEquals(4, version);
        verify(jedis).exists(key);
        verify(jedis).incr(key);
    }

    @Test
    void testIncrement_KeyDoesNotExist() {
        when(jedis.exists(key)).thenReturn(false);
        when(jedis.incr(key)).thenReturn(1L);

        long version = tokenVersionDao.increment(userId, tokenCategory, tokenType);

        assertEquals(1, version);
        verify(jedis).exists(key);
        verify(jedis).set(key, "0");
        verify(jedis).incr(key);
    }
}
