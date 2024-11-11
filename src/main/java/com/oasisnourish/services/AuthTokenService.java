package com.oasisnourish.services;

import java.util.Optional;

import com.oasisnourish.models.AuthToken;

public interface AuthTokenService {
    Optional<AuthToken> findToken(String token);
    void createToken(int userId, String tokenType); 
    void deleteToken(String token);   
}
