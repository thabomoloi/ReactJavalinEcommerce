package com.oasisnourish.dao;

import java.util.List;

import com.oasisnourish.models.AuthToken;

public interface TokenRateLimitDao {

    long find(int userId);

    long increment(int userId, int expires);

    void reset(int userId);

    List<AuthToken> findAllTokens(int userId);
}
