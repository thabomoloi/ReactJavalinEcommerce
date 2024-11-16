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
    public Optional<T> findToken(String token) {
        return tokenDao.findToken(token);
    }

    @Override
    public void deleteToken(String token) {
        tokenDao.deleteToken(token);
    }

}
