package com.oasisnourish.db;

import redis.clients.jedis.JedisPooled;

/**
 * Interface for Redis connection handling.
 * Provides a method to retrieve a Redis connection.
 */
public interface RedisConnection {

    /**
     * Retrieves a {@link JedisPooled} instance to interact with Redis.
     *
     * @return a {@link JedisPooled} instance for Redis operations.
     */
    JedisPooled getJedis();
}