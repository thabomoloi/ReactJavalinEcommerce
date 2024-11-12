package com.oasisnourish.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            String key = getKey(tokenDetails.getToken());
            jedis.hset(key, "tokenCategory", tokenDetails.getTokenCategory());
            jedis.hset(key, "tokenType", tokenDetails.getTokenType());
            jedis.hset(key, "tokenVersion", String.valueOf(tokenDetails.getTokenVersion()));
            jedis.hset(key, "expires", String.valueOf(tokenDetails.getExpires()));
            jedis.hset(key, "userId", String.valueOf(tokenDetails.getUserId()));
            jedis.expire(key, (ttl / 1000));
        }
    }

    @Override
    public Optional<Token> findToken(String token) {
        try (JedisPooled jedis = redisConnection.getJedis();) {
            String key = getKey(token);
            if (jedis.exists(key)) {
                String tokenCategory = jedis.hget(key, "tokenCategory");
                String tokenType = jedis.hget(key, "tokenType");
                long tokenVersion = Long.parseLong(jedis.hget(key, "tokenVersion"));
                long expires = Long.parseLong(jedis.hget(key, "expires"));
                int userId = Integer.parseInt(jedis.hget(key, "userId"));
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
    public void deleteToken(String token) {
        try (JedisPooled jedis = redisConnection.getJedis();) {
            jedis.del(getKey(token));
        }
    }

    private String getKey(String token) {
        return "token:" + token;
    }

    @Override
    public List<Token> findTokensByUserId(int userId) {
        List<Token> tokens = new ArrayList<>();
        try (JedisPooled jedis = redisConnection.getJedis()) {
            Set<String> keys = jedis.keys("token:*");
            for (String key : keys) {
                String tokenType = jedis.hget(key, "tokenType");
                long tokenVersion = Long.parseLong(jedis.hget(key, "tokenVersion"));
                long expires = Long.parseLong(jedis.hget(key, "expires"));
                int uid = Integer.parseInt(jedis.hget(key, "userId"));
                String tokenCategory = jedis.hget(key, "tokenCategory");
                if (uid == userId) {
                    switch (tokenCategory) {
                        case "auth" ->
                            tokens.add(new AuthToken(key, tokenType, tokenVersion, expires, uid));
                        case "jwt" ->
                            tokens.add(new JsonWebToken(key, tokenType, tokenVersion, expires, uid));
                        default -> {
                        }
                    }
                }
            }
            return tokens;
        }
    }
}
