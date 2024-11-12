package com.oasisnourish.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.dao.TokenDao;
import com.oasisnourish.dao.TokenVersionDao;
import com.oasisnourish.models.JsonWebToken;
import com.oasisnourish.models.User;
import com.oasisnourish.services.JWTService;
import com.oasisnourish.util.jwt.JWTProvider;

public class JWTServiceImpl extends TokenServiceImpl<JsonWebToken> implements JWTService {

    private final TokenVersionDao tokenVersionDao;
    private final JWTProvider provider;

    public JWTServiceImpl(TokenDao<JsonWebToken> tokenDao, TokenVersionDao tokenVersionDao, JWTProvider provider) {
        super(tokenDao);
        this.tokenVersionDao = tokenVersionDao;
        this.provider = provider;
    }

    @Override
    public Map<String, JsonWebToken> createTokens(User user) {
        provider.updateJwtCurrentTime();

        long tokenVersion = tokenVersionDao.increment(user.getId(), "jwt", "access");
        JsonWebToken accessToken = provider.generateToken(user, "access", tokenVersion);
        tokenVersion = tokenVersionDao.increment(user.getId(), "jwt", "refresh");
        JsonWebToken refreshToken = provider.generateToken(user, "refresh", tokenVersion);
        tokenDao.saveToken(accessToken);
        tokenDao.saveToken(refreshToken);

        return new HashMap<>() {
            {
                put("JWT_ACCESS_TOKEN", accessToken);
                put("JWT_REFRESH_TOKEN", refreshToken);
            }
        };
    }

    @Override
    public Optional<DecodedJWT> decodeToken(String token) {
        if (tokenDao.findToken(token).isPresent()) {
            return provider.validateToken(token);
        }
        return Optional.empty();
    }

    @Override
    public long getCurrentTokenVersion(int userId, String tokenType) {
        return tokenVersionDao.find(userId, "jwt", tokenType);
    }

}
