package com.oasisnourish.dao.impl;

import java.util.Optional;

import com.oasisnourish.dao.TokenDao;
import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.models.JWTToken;

import redis.clients.jedis.JedisPooled;

public class JWTTokenDaoImpl implements TokenDao<JWTToken> {

    private final RedisConnection redisConnection;

    public JWTTokenDaoImpl(RedisConnection redisConnection) {
        this.redisConnection = redisConnection;
    }


    @Override
    public void saveToken(JWTToken tokenDetails) {
        long ttl = tokenDetails.getExpires() - System.currentTimeMillis();
        
        if (ttl <= 0) {
            return; // Token has already expired
        }

        try(JedisPooled jedis = redisConnection.getJedis();){
            String key = "jwt:" + tokenDetails.getToken();
            jedis.hset(key, "tokenType", tokenDetails.getTokenType());
            jedis.hset(key, "tokenVersion", String.valueOf(tokenDetails.getTokenVersion()));
            jedis.hset(key, "expires", String.valueOf(tokenDetails.getExpires()));
            jedis.expire(key, (ttl / 1000));
        }
    }

    @Override
    public Optional<JWTToken> findToken(String token) {
        try(JedisPooled jedis = redisConnection.getJedis();){
            String key = "jwt:" + token;
            if (jedis.exists(key)) {
                String tokenType = jedis.hget(key, "tokenType");
                long tokenVersion = Long.parseLong(jedis.hget(key, "tokenVersion"));
                long expires = Long.parseLong(jedis.hget(key, "expires"));
                return  Optional.of(new JWTToken(token, tokenType, tokenVersion, expires));
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public void deleteToken(String token) {
        try(JedisPooled jedis = redisConnection.getJedis();){
            jedis.del("jwt:" + token);
        }
    }
    
}
