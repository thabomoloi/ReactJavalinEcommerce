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

public class TokenDaoImpl<T extends Token> implements TokenDao<T> {

    private final RedisConnection redisConnection;
    private final Class<T> tokenClass;

    public TokenDaoImpl(RedisConnection redisConnection, Class<T> tokenClass) {
        this.redisConnection = redisConnection;
        this.tokenClass = tokenClass;
    }

    @Override
    public void saveToken(T tokenDetails) {
        Duration ttl = Duration.between(Instant.now(), tokenDetails.getExpires());

        if (ttl.isNegative() || ttl.isZero()) {
            return; // Token has already expired
        }

        var jedis = redisConnection.getJedis();
        String key = getKey(tokenDetails.getToken());
        jedis.hset(key, "token", tokenDetails.getToken());
        jedis.hset(key, "tokenCategory", tokenDetails.getTokenCategory().getCategory());
        jedis.hset(key, "tokenType", tokenDetails.getTokenType().getType());
        jedis.hset(key, "tokenVersion", String.valueOf(tokenDetails.getTokenVersion()));
        jedis.hset(key, "expires", String.valueOf(tokenDetails.getExpires()));
        jedis.hset(key, "userId", String.valueOf(tokenDetails.getUserId()));
        jedis.expire(key, ttl.toSeconds());
    }

    @Override
    public Optional<T> findToken(String token) {
        var jedis = redisConnection.getJedis();

        String key = getKey(token);
        if (jedis.exists(key)) {
            return buildTokenFromRedis(key, jedis);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteToken(String token) {
        redisConnection.getJedis().del(getKey(token));
    }

    @Override
    public List<T> findTokensByUserId(int userId) {
        var jedis = redisConnection.getJedis();

        List<T> tokens = new ArrayList<>();
        Set<String> keys = jedis.keys("token:*");
        for (String key : keys) {
            if (userIdMatches(jedis, key, userId)) {
                buildTokenFromRedis(key, jedis).ifPresent((tokenObj) -> {
                    tokens.add(tokenObj);
                });
            }
        }
        return tokens;
    }

    private Optional<T> buildTokenFromRedis(String key, JedisPooled jedis) {
        String token = jedis.hget(key, "token");
        String tokenCategoryStr = jedis.hget(key, "tokenCategory");
        String tokenTypeStr = jedis.hget(key, "tokenType");
        long tokenVersion = Long.parseLong(jedis.hget(key, "tokenVersion"));
        Instant expires = Instant.parse(jedis.hget(key, "expires"));
        int userId = Integer.parseInt(jedis.hget(key, "userId"));

        Tokens.Category tokenCategory = Tokens.Category.valueOf(tokenCategoryStr.toUpperCase());

        Token tokenObj = switch (tokenCategory) {
            case AUTH ->
                new AuthToken(token, Tokens.Auth.valueOf(tokenTypeStr.toUpperCase()), tokenVersion, expires, userId);
            case JWT ->
                new JsonWebToken(token, Tokens.Jwt.valueOf(tokenTypeStr.toUpperCase()), tokenVersion, expires, userId);
        };

        if (tokenClass.isInstance(tokenObj)) {
            return Optional.of(tokenClass.cast(tokenObj));
        }
        return Optional.empty();
    }

    private boolean userIdMatches(JedisPooled jedis, String key, int userId) {
        return Integer.parseInt(jedis.hget(key, "userId")) == userId;
    }

    private String getKey(String token) {
        return "token:" + token;
    }
}
