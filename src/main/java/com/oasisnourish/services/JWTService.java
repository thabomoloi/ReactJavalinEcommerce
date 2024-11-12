package com.oasisnourish.services;

import java.util.Map;
import java.util.Optional;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.models.JsonWebToken;
import com.oasisnourish.models.User;

public interface JWTService extends TokenService<JsonWebToken> {

    Map<String, JsonWebToken> createTokens(User user);

    Optional<DecodedJWT> decodeToken(String token);

    long getCurrentTokenVersion(int userId, String tokenType);

}
