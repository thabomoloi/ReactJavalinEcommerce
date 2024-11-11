package com.oasisnourish.services.impl;

import java.util.Optional;

import com.oasisnourish.dao.TokenDao;
import com.oasisnourish.services.TokenService;

public abstract class TokenServiceImpl<T> implements TokenService<T> {

    protected TokenDao<T> tokenDao;

    public TokenServiceImpl(TokenDao<T> tokenDao) {
        this.tokenDao = tokenDao;
    }

    @Override
    public Optional<T> findToken(int userId, String token) {
        return tokenDao.findToken(token, userId);
    }

    @Override
    public abstract void createToken(int userId, String tokenType);

    @Override
    public void deleteToken(int userId, String token) {
        tokenDao.deleteToken(token, userId);
    }

}
