package com.oasisnourish.dao.tokens;

public interface TokenRateLimitDao {

    long find(int userId);

    long increment(int userId, int expires);

    void reset(int userId);

    long ttl(int userId);

}
