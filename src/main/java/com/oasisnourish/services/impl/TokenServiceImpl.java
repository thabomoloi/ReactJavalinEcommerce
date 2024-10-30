package com.oasisnourish.services.impl;

import java.util.UUID;

import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.exceptions.TooManyRequestsException;
import com.oasisnourish.services.TokenService;

public class TokenServiceImpl implements TokenService {
    private static final int MAX_TOKENS_PER_DAY = 5;
    private static final int RATE_LIMIT_WINDOW_SECONDS = 24 * 60 * 60; // 24 hours
    private static final int TOKEN_EXPIRES_SECONDS = 30 * 60; // 1 hour
    private final RedisConnection redisConnection;

    public TokenServiceImpl(RedisConnection redisConnection) {
        this.redisConnection = redisConnection;
    }

    @Override
    public String generateToken(int userId, String tokenType) throws TooManyRequestsException {
        var jedis = redisConnection.getJedis();
        String rateLimitKey = "rate_limit:" + userId + ":" + tokenType;
        String tokenKey = "tokens:" + userId + ":" + tokenType;

        // Check if user has hit the daily rate limit
        // String requestCount = jedis.get(rateLimitKey);
        // if (requestCount != null && Integer.parseInt(requestCount) >=
        // MAX_TOKENS_PER_DAY) {
        // throw new TooManyRequestsException("Too many requests. Try again after 24
        // hours.");
        // }

        // Generate a new token
        String token = UUID.randomUUID().toString();

        // Revoke the last token if it exists
        jedis.del(tokenKey);

        // Store the new token with expiration
        jedis.setex(tokenKey, TOKEN_EXPIRES_SECONDS, token);

        // Increment rate limit counter with 24-hour expiration
        jedis.incr(rateLimitKey);
        jedis.expire(rateLimitKey, RATE_LIMIT_WINDOW_SECONDS);

        return token;
    }

    @Override
    public boolean verifyToken(int userId, String tokenType, String token) {
        var jedis = redisConnection.getJedis();
        String tokenKey = "tokens:" + userId + ":" + tokenType;
        String storedToken = jedis.get(tokenKey);
        return token.equals(storedToken);
    }

    @Override
    public void revokeToken(int userId, String tokenType) {
        var jedis = redisConnection.getJedis();
        String tokenKey = "tokens:" + userId + ":" + tokenType;
        jedis.del(tokenKey);
    }

}
