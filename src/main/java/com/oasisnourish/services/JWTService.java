package com.oasisnourish.services;

import java.util.Map;
import java.util.Optional;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.models.User;

import javalinjwt.JWTProvider;

/**
 * JWTService interface for handling operations related to JSON Web Tokens (JWT)
 * within the authentication system. This service includes token generation,
 * validation, revocation, and management of token expiration.
 */
public interface JWTService {

    /**
     * Gets the expiration time for a specified token type.
     *
     * @param tokenType The type of token (e.g., "access" or "refresh").
     * @return The expiration time of the token in minutes.
     */
    int getTokenExpires(String tokenType);

    /**
     * Retrieves the JWT provider for managing JWTs.
     *
     * @return An instance of {@link JWTProvider} configured for {@link User}
     *         tokens.
     */
    JWTProvider<User> getProvider();

    /**
     * Generates access and refresh tokens for a given user.
     *
     * @param user The user for whom tokens are to be generated.
     * @return A map containing the access and refresh tokens as key-value pairs.
     */
    Map<String, String> generateTokens(User user);

    /**
     * Validates a given token to ensure it is well-formed, unexpired, and has not
     * been revoked.
     *
     * @param token The JWT string to validate.
     * @return True if the token is valid; otherwise, false.
     */
    boolean isTokenValid(String token);

    /**
     * Revokes a specific token, preventing it from being accepted in future
     * requests.
     *
     * @param token The JWT string to revoke.
     */
    void revokeToken(String token);

    /**
     * Retrieves a decoded JWT if the token is valid and well-formed.
     *
     * @param token The JWT string to decode.
     * @return An {@link Optional} containing the {@link DecodedJWT} if valid, or an
     *         empty Optional if invalid.
     */
    Optional<DecodedJWT> getToken(String token);

    /**
     * Retrieves the version of the token associated with a given user.
     *
     * @param userId The unique identifier of the user.
     * @return The version number of the token.
     */
    long getTokenVersion(int userId);

    /**
     * Deletes a specific token from storage or cache, making it inactive.
     *
     * @param token The JWT string to delete.
     */
    void deleteToken(String token);
}
