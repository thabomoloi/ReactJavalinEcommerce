package com.oasisnourish.services.impl;

import java.util.UUID;

import com.oasisnourish.config.AuthTokenConfig;
import com.oasisnourish.dao.TokenDao;
import com.oasisnourish.dao.TokenRateLimitDao;
import com.oasisnourish.dao.TokenVersionDao;
import com.oasisnourish.exceptions.TooManyRequestsException;
import com.oasisnourish.models.AuthToken;
import static com.oasisnourish.util.TimeFormatter.formatSecondsToReadableTime;

public class AuthTokenServiceImpl extends TokenServiceImpl<AuthToken> {

    private final TokenVersionDao tokenVersionDao;
    private final TokenRateLimitDao tokenRateLimitDao;
    private final AuthTokenConfig tokenConfig;

    public AuthTokenServiceImpl(TokenDao<AuthToken> tokenDao, TokenVersionDao tokenVersionDao, TokenRateLimitDao tokenRateLimitDao, AuthTokenConfig tokenConfig) {
        super(tokenDao);
        this.tokenVersionDao = tokenVersionDao;
        this.tokenRateLimitDao = tokenRateLimitDao;
        this.tokenConfig = tokenConfig;
    }

    @Override
    public void createToken(int userId, String tokenType) {
        long tokenRequestCount = tokenRateLimitDao.find(userId);
        if (tokenRequestCount > tokenConfig.getMaxTokensPerWindow()) {
            throw new TooManyRequestsException("Too many requests. Try again after " + formatSecondsToReadableTime(tokenConfig.getRateLimitWindow()));
        }

        var previousTokens = tokenRateLimitDao.findAllTokens(userId);
        for (AuthToken previousToken : previousTokens) {
            if (previousToken.getTokenType().equals(tokenType)) {
                tokenDao.deleteToken(previousToken.getToken(), userId);
            }
        }

        long tokenExpiry = System.currentTimeMillis() + tokenConfig.getTokenExpires() * 1000;
        long tokenVersion = tokenVersionDao.find(userId, "auth", tokenType);

        AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), tokenType, tokenVersion, tokenExpiry, userId);
        tokenDao.saveToken(authToken);

        tokenVersionDao.increment(userId, "auth", tokenType);
        tokenRateLimitDao.increment(userId, tokenConfig.getTokenExpires());
    }
}
