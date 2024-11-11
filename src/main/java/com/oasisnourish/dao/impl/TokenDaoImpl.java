package com.oasisnourish.dao.impl;

import java.util.Optional;

import com.oasisnourish.dao.TokenDao;
import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.models.AuthToken;
import com.oasisnourish.models.JsonWebToken;
import com.oasisnourish.models.Token;

import redis.clients.jedis.JedisPooled;

public class TokenDaoImpl implements TokenDao<Token> {

    private final RedisConnection redisConnection;

    public TokenDaoImpl(RedisConnection redisConnection) {
        this.redisConnection = redisConnection;
    }

    @Override
    public void saveToken(Token tokenDetails) {
        long ttl = tokenDetails.getExpires() - System.currentTimeMillis();

        if (ttl <= 0) {
            return; // Token has already expired
        }

        try (JedisPooled jedis = redisConnection.getJedis();) {
            String key = getKey(tokenDetails.getUserId(), tokenDetails.getToken());
            jedis.hset(key, "tokenCategory", tokenDetails.getTokenCategory());
            jedis.hset(key, "tokenType", tokenDetails.getTokenType());
            jedis.hset(key, "tokenVersion", String.valueOf(tokenDetails.getTokenVersion()));
            jedis.hset(key, "expires", String.valueOf(tokenDetails.getExpires()));
            jedis.hset(key, "userId", String.valueOf(((AuthToken) tokenDetails).getUserId()));
            jedis.expire(key, (ttl / 1000));
        }
    }

    @Override
    public Optional<Token> findToken(String token, int userId) {
        try (JedisPooled jedis = redisConnection.getJedis();) {
            String key = getKey(userId, token);
            if (jedis.exists(key)) {
                String tokenCategory = jedis.hget(key, "tokenCategory");
                String tokenType = jedis.hget(key, "tokenType");
                long tokenVersion = Long.parseLong(jedis.hget(key, "tokenVersion"));
                long expires = Long.parseLong(jedis.hget(key, "expires"));
                return switch (tokenCategory) {
                    case "auth" ->
                        Optional.of(new AuthToken(token, tokenType, tokenVersion, expires, userId));
                    case "jwt" ->
                        Optional.of(new JsonWebToken(token, tokenType, tokenVersion, expires, userId));
                    default ->
                        Optional.empty();
                };
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public void deleteToken(String token, int userId) {
        try (JedisPooled jedis = redisConnection.getJedis();) {
            jedis.del(getKey(userId, token));
        }
    }

    private String getKey(int userId, String token) {
        return "user:" + userId + ":token:" + token;
    }

}
