package com.oasisnourish.dao.impl;

import com.oasisnourish.dao.TokenVersionDao;
import com.oasisnourish.db.RedisConnection;

import redis.clients.jedis.JedisPooled;

public class TokenVersionDaoImpl implements TokenVersionDao {

    private final RedisConnection redisConnection;

    public TokenVersionDaoImpl(RedisConnection redisConnection) {
        this.redisConnection = redisConnection;
    }

    @Override
    public long find(int userId, String tokenCategory, String tokenType) {
        String key = "token-version:" + userId + ":" + tokenCategory + ":" + tokenType;
        try (JedisPooled jedis = redisConnection.getJedis()) {
            if (!jedis.exists(key)) {
                jedis.set(key, "1");
            }
            return Long.parseLong(jedis.get(key));
        }
    }

    @Override
    public long increment(int userId, String tokenCategory, String tokenType) {
        String key = "token-version:" + userId + ":" + tokenCategory + ":" + tokenType;
        try (JedisPooled jedis = redisConnection.getJedis()) {
            if (!jedis.exists(key)) {
                jedis.set(key, "0");
            }
            return jedis.incr(key);
        }
    }
}
