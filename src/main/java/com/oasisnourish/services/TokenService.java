package com.oasisnourish.services;

import java.util.Optional;

public interface TokenService<T> {

    Optional<T> findToken(String token);

    void deleteToken(String token);
}
