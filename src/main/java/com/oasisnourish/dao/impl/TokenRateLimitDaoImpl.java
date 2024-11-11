package com.oasisnourish.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.oasisnourish.dao.TokenRateLimitDao;
import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.models.AuthToken;

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
        String key = getKey(userId);
        try (JedisPooled jedis = redisConnection.getJedis();) {
            jedis.del(key);
        }
    }

    @Override
    public List<AuthToken> findAllTokens(int userId) {
        List<AuthToken> tokens = new ArrayList<>();
        String pattern = "user:" + userId + ":token:*";
        try (JedisPooled jedis = redisConnection.getJedis()) {
            Set<String> keys = jedis.keys(pattern);
            for (String key : keys) {
                String tokenType = jedis.hget(key, "tokenType");
                long tokenVersion = Long.parseLong(jedis.hget(key, "tokenVersion"));
                long expires = Long.parseLong(jedis.hget(key, "expires"));
                int uid = Integer.parseInt(jedis.hget(key, "userId"));
                tokens.add(new AuthToken(key, tokenType, tokenVersion, expires, uid));
            }
        }
        return tokens;
    }

    private String getKey(int userId) {
        return "user:" + userId + ":token-rate-limit";
    }
}
