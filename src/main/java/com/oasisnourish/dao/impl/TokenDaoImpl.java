package com.oasisnourish.dao.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.oasisnourish.dao.TokenDao;
import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.enums.Tokens;
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
        Duration ttl = Duration.between(Instant.now(), tokenDetails.getExpires());

        if (ttl.isNegative() || ttl.isZero()) {
            return; // Token has already expired
        }

        try (JedisPooled jedis = redisConnection.getJedis();) {
            String key = getKey(tokenDetails.getToken());
            jedis.hset(key, "token", tokenDetails.getToken());
            jedis.hset(key, "tokenCategory", tokenDetails.getTokenCategory().getCategory());
            jedis.hset(key, "tokenType", tokenDetails.getTokenType().getType());
            jedis.hset(key, "tokenVersion", String.valueOf(tokenDetails.getTokenVersion()));
            jedis.hset(key, "expires", String.valueOf(tokenDetails.getExpires()));
            jedis.hset(key, "userId", String.valueOf(tokenDetails.getUserId()));
            jedis.expire(key, ttl.toSeconds());
        }
    }

    @Override
    public Optional<Token> findToken(String token) {
        try (JedisPooled jedis = redisConnection.getJedis();) {
            String key = getKey(token);
            if (jedis.exists(key)) {
                return Optional.of(buildTokenFromRedis(key, jedis));
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

    @Override
    public List<Token> findTokensByUserId(int userId) {
        List<Token> tokens = new ArrayList<>();
        try (JedisPooled jedis = redisConnection.getJedis()) {
            Set<String> keys = jedis.keys("token:*");
            for (String key : keys) {
                if (userIdMatches(jedis, key, userId)) {
                    tokens.add(buildTokenFromRedis(key, jedis));
                }
            }
            return tokens;
        }
    }

    private Token buildTokenFromRedis(String key, JedisPooled jedis) {
        String token = jedis.hget(key, "token");
        String tokenCategoryStr = jedis.hget(key, "tokenCategory");
        String tokenTypeStr = jedis.hget(key, "tokenType");
        long tokenVersion = Long.parseLong(jedis.hget(key, "tokenVersion"));
        Instant expires = Instant.parse(jedis.hget(key, "expires"));
        int userId = Integer.parseInt(jedis.hget(key, "userId"));

        Tokens.Category tokenCategory = Tokens.Category.valueOf(tokenCategoryStr.toUpperCase());

        Tokens.Type tokenType = switch (tokenCategory) {
            case AUTH ->
                Tokens.Auth.valueOf(tokenTypeStr.toUpperCase());
            case JWT ->
                Tokens.Jwt.valueOf(tokenTypeStr.toUpperCase());
        };

        return switch (tokenCategory) {
            case AUTH ->
                new AuthToken(token, (Tokens.Auth) tokenType, tokenVersion, expires, userId);
            case JWT ->
                new JsonWebToken(token, (Tokens.Jwt) tokenType, tokenVersion, expires, userId);
        };
    }

    private boolean userIdMatches(JedisPooled jedis, String key, int userId) {
        return Integer.parseInt(jedis.hget(key, "userId")) == userId;
    }

    private String getKey(String token) {
        return "token:" + token;
    }
}
