package com.oasisnourish.services.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.oasisnourish.config.AuthTokenConfig;
import com.oasisnourish.dao.TokenDao;
import com.oasisnourish.dao.TokenRateLimitDao;
import com.oasisnourish.dao.TokenVersionDao;
import com.oasisnourish.enums.Tokens;
import com.oasisnourish.exceptions.TooManyRequestsException;
import com.oasisnourish.models.AuthToken;
import com.oasisnourish.services.AuthTokenService;
import com.oasisnourish.util.TimeFormatter;

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
    public AuthToken createToken(int userId, Tokens.Auth tokenType) {
        long tokenRequestCount = tokenRateLimitDao.find(userId);
        if (tokenRequestCount > tokenConfig.getMaxTokensPerWindow()) {
            throw new TooManyRequestsException("Too many requests. Try again after " + new TimeFormatter().format(tokenRateLimitDao.ttl(userId)) + ".");
        }

        List<AuthToken> previousTokens = tokenDao.findTokensByUserId(userId)
                .stream()
                .filter(token -> Tokens.Category.AUTH == token.getTokenCategory())
                .collect(Collectors.toList());

        for (AuthToken previousToken : previousTokens) {
            if (previousToken.getTokenType().equals(tokenType)) {
                tokenDao.deleteToken(previousToken.getToken());
            }
        }

        Instant tokenExpiry = Instant.now().plusSeconds(tokenConfig.getTokenExpires());
        long tokenVersion = tokenVersionDao.find(userId, Tokens.Category.AUTH, tokenType);

        AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), tokenType, tokenVersion, tokenExpiry, userId);
        tokenDao.saveToken(authToken);

        tokenVersionDao.increment(userId, Tokens.Category.AUTH, tokenType);
        tokenRateLimitDao.increment(userId, tokenConfig.getTokenExpires());

        return authToken;
    }
}
