package com.oasisnourish.services.tokens;

import com.oasisnourish.enums.Tokens;
import com.oasisnourish.models.tokens.AuthToken;

public interface AuthTokenService extends TokenService<AuthToken> {

    AuthToken createToken(int userId, Tokens.Auth tokenType);
}
