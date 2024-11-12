package com.oasisnourish.dao.impl;

import com.oasisnourish.dao.TokenRateLimitDao;
import com.oasisnourish.db.RedisConnection;

import redis.clients.jedis.JedisPooled;

public class TokenRateLimitDaoImpl implements TokenRateLimitDao {

    private final RedisConnection redisConnection;

    public TokenRateLimitDaoImpl(RedisConnection redisConnection) {
        this.redisConnection = redisConnection;
    }

    @Override
    public long find(int userId) {
        String key = getKey(userId);
        try (JedisPooled jedis = redisConnection.getJedis()) {
            if (!jedis.exists(key)) {
                jedis.set(key, "1");
            }
            return Long.parseLong(jedis.get(key));
        }
    }

    @Override
    public long increment(int userId, int expires) {
        String key = getKey(userId);
        try (JedisPooled jedis = redisConnection.getJedis()) {
            if (!jedis.exists(key)) {
                jedis.set(key, "0");
            }
            jedis.expire(key, expires);
            return jedis.incr(key);
        }
    }

    @Override
    public void reset(int userId) {
        try (JedisPooled jedis = redisConnection.getJedis();) {
            jedis.del(getKey(userId));
        }
    }

    @Override
    public long ttl(int userId) {
        try (JedisPooled jedis = redisConnection.getJedis();) {
            return jedis.ttl(getKey(userId));
        }
    }

    private String getKey(int userId) {
        return "user:" + userId + ":token-rate-limit";
    }

}
