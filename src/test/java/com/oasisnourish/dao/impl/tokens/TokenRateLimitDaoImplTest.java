package com.oasisnourish.dao.impl.tokens;

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
    public void testTtl() {
        int userId = 123;
        String key = "user:" + userId + ":token-rate-limit";
        long expectedTtl = 120L;

        when(jedis.ttl(key)).thenReturn(expectedTtl);

        long result = tokenRateLimitDao.ttl(userId);

        verify(jedis).ttl(key);
        assertEquals(expectedTtl, result);
    }
}
