package com.oasisnourish.services;

import java.util.Optional;

public interface TokenService<T> {

    Optional<T> findToken(int userId, String token);

    void createToken(int userId, String tokenType);

    void deleteToken(int userId, String token);
}
