package com.oasisnourish.services;

import com.oasisnourish.enums.Tokens;
import com.oasisnourish.models.AuthToken;

public interface AuthTokenService extends TokenService<AuthToken> {

    AuthToken createToken(int userId, Tokens.Auth tokenType);
}
