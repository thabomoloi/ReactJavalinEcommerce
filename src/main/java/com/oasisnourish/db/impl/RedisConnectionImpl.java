package com.oasisnourish.db.impl;

import com.oasisnourish.config.EnvConfig;
import com.oasisnourish.config.RedisDbConfig;
import com.oasisnourish.db.RedisConnection;

import io.github.cdimascio.dotenv.Dotenv;
import redis.clients.jedis.JedisPooled;

/**
 * Implementation of {@link RedisConnection} using Jedis for Redis connections.
 * This class handles the setup and retrieval of Redis connections.
 */
public class RedisConnectionImpl implements RedisConnection {

    private static final Dotenv dotenv = EnvConfig.getDotenv();
    private static final JedisPooled jedis;

    static {
        jedis = setUpRedisConnection();
    }

    /**
     * Sets up the Redis connection using Jedis configuration.
     *
     * @return a JedisPooled instance configured for Redis.
     * @throws IllegalStateException if Redis environment variables are not set.
     */
    private static JedisPooled setUpRedisConnection() {
        var redisDbConfig = new RedisDbConfig(dotenv);
        return new JedisPooled(redisDbConfig.getHost(), redisDbConfig.getPort());
    }

    @Override
    public JedisPooled getJedis() {
        return jedis;
    }
}
