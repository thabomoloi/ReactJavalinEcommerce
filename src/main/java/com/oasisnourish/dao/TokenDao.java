package com.oasisnourish.dao;

import java.util.Optional;

public interface TokenDao<T> {
    void saveToken(T tokenDetails);
    Optional<T> findToken(String token);
    void deleteToken(String token);
}
