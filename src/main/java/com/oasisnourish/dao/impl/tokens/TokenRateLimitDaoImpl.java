package com.oasisnourish.dao.impl.tokens;

import com.oasisnourish.dao.tokens.TokenRateLimitDao;
import com.oasisnourish.db.RedisConnection;

public class TokenRateLimitDaoImpl implements TokenRateLimitDao {

    private final RedisConnection redisConnection;

    public TokenRateLimitDaoImpl(RedisConnection redisConnection) {
        this.redisConnection = redisConnection;
    }

    @Override
    public long find(int userId) {
        String key = getKey(userId);
        var jedis = redisConnection.getJedis();

        if (!jedis.exists(key)) {
            jedis.set(key, "1");
        }
        return Long.parseLong(jedis.get(key));
    }

    @Override
    public long increment(int userId, int expires) {
        var jedis = redisConnection.getJedis();

        String key = getKey(userId);
        if (!jedis.exists(key)) {
            jedis.set(key, "0");
        }
        jedis.expire(key, expires);
        return jedis.incr(key);
    }

    @Override
    public void reset(int userId) {
        redisConnection.getJedis().del(getKey(userId));
    }

    @Override
    public long ttl(int userId) {
        return redisConnection.getJedis().ttl(getKey(userId));
    }

    private String getKey(int userId) {
        return "user:" + userId + ":token-rate-limit";
    }

}
