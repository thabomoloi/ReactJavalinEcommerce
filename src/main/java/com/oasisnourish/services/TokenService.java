package com.oasisnourish.services;

import com.oasisnourish.exceptions.InvalidTokenException;
import com.oasisnourish.exceptions.TooManyRequestsException;

public interface TokenService {
    /**
     * Generates and stores a token with expiration for a specific user and token
     * type.
     * 
     * @param userId    - The user for whom the token is generated.
     * @param tokenType - The type of the token (e.g., "confirmation",
     *                  "password-reset").
     * @return The generated token.
     * @throws TooManyRequestsException if the user has generated too many tokens
     *                                  recently.
     */
    String generateToken(int userId, String tokenType) throws TooManyRequestsException;

    /**
     * Verifies if a token is valid for a user and token type.
     * 
     * @param userId    - The user to verify the token for.
     * @param tokenType - The type of token.
     * @param token     - The token to verify.
     * @return true if the token is valid; false otherwise.
     */
    boolean verifyToken(int userId, String tokenType, String token);

    /**
     * Revokes the last token for a user and token type.
     * 
     * @param userId    - The user for whom to revoke the token.
     * @param tokenType - The type of token to revoke.
     */
    void revokeToken(int userId, String tokenType);

    /**
     * Verifies the token and throws InvalidTokenException if it's invalid.
     *
     * @param userId    the ID of the user
     * @param tokenType the type of token (confirmation, reset-password)
     * @param token     the token to verify
     * @throws InvalidTokenException Throws this exception when the token is invalid
     *                               or has expired.
     */
    public void verifyTokenOrThrow(int userId, String tokenType, String token) throws InvalidTokenException;
}
