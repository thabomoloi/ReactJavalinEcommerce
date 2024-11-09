package com.oasisnourish.dao.impl;

import java.util.Optional;

import com.oasisnourish.dao.TokenDao;
import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.models.AuthToken;

import redis.clients.jedis.JedisPooled;

public class AuthTokenDaoImpl implements TokenDao<AuthToken> {
    private final RedisConnection redisConnection;

    public AuthTokenDaoImpl(RedisConnection redisConnection) {
        this.redisConnection = redisConnection;
    }

    @Override
    public void saveToken(AuthToken tokenDetails) {
        long ttl = tokenDetails.getExpires() - System.currentTimeMillis();
        
        if (ttl <= 0) {
            return; // Token has already expired
        }

        try(JedisPooled jedis = redisConnection.getJedis();){
            String key = "jwt:" + tokenDetails.getToken();
            jedis.hset(key, "userId", String.valueOf(tokenDetails.getUserId()));
            jedis.hset(key, "expires", String.valueOf(tokenDetails.getExpires()));
            jedis.hset(key, "tokenType", tokenDetails.getTokenType());
            jedis.expire(key, (ttl / 1000));
        }
    }

    @Override
    public Optional<AuthToken> findToken(String token) {
        try(JedisPooled jedis = redisConnection.getJedis();){
            String key = "auth-token:" + token;
            if (jedis.exists(key)) {
                int userId = Integer.parseInt(jedis.hget(key, "userId"));
                long expires = Long.parseLong(jedis.hget(key, "expires"));
                String tokenType = jedis.hget(key, "tokenType");
                return Optional.of(new AuthToken(token, userId, expires, tokenType));
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public void deleteToken(String token) {
        try(JedisPooled jedis = redisConnection.getJedis();){
            jedis.del("auth-token:" + token);
        }
    }
}
