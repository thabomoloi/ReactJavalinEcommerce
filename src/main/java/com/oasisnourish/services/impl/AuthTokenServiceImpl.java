package com.oasisnourish.services.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.oasisnourish.config.AuthTokenConfig;
import com.oasisnourish.dao.TokenDao;
import com.oasisnourish.dao.TokenRateLimitDao;
import com.oasisnourish.dao.TokenVersionDao;
import com.oasisnourish.exceptions.TooManyRequestsException;
import com.oasisnourish.models.AuthToken;
import com.oasisnourish.services.AuthTokenService;
import static com.oasisnourish.util.TimeFormatter.format;

public class AuthTokenServiceImpl extends TokenServiceImpl<AuthToken> implements AuthTokenService {

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
    public AuthToken createToken(int userId, String tokenType) {
        long tokenRequestCount = tokenRateLimitDao.find(userId);
        if (tokenRequestCount > tokenConfig.getMaxTokensPerWindow()) {
            throw new TooManyRequestsException("Too many requests. Try again after " + format(tokenRateLimitDao.ttl(userId)) + ".");
        }

        List<AuthToken> previousTokens = tokenDao.findTokensByUserId(userId)
                .stream()
                .filter(token -> "auth".equals(token.getTokenCategory()))
                .collect(Collectors.toList());

        for (AuthToken previousToken : previousTokens) {
            if (previousToken.getTokenType().equals(tokenType)) {
                tokenDao.deleteToken(previousToken.getToken());
            }
        }

        long tokenExpiry = System.currentTimeMillis() + tokenConfig.getTokenExpires() * 1000;
        long tokenVersion = tokenVersionDao.find(userId, "auth", tokenType);

        AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), tokenType, tokenVersion, tokenExpiry, userId);
        tokenDao.saveToken(authToken);

        tokenVersionDao.increment(userId, "auth", tokenType);
        tokenRateLimitDao.increment(userId, tokenConfig.getTokenExpires());

        return authToken;
    }
}
