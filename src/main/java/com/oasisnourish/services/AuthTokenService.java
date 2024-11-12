package com.oasisnourish.services;

import com.oasisnourish.models.AuthToken;

public interface AuthTokenService extends TokenService<AuthToken> {

    AuthToken createToken(int userId, String tokenType);
}
