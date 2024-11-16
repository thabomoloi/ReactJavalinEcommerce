package com.oasisnourish.util.jwt;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.enums.Role;
import com.oasisnourish.enums.Tokens;
import com.oasisnourish.models.User;

public class JWTGeneratorTest {

    private final User user = new User(1, "John Doe", "john.doe@test.com", "encodedPassword", Role.ADMIN);
    private final Algorithm algorithm = Algorithm.HMAC256("testSecret");
    private final JWTGenerator jwtGenerator = new JWTGenerator();

    @Test
    void generate_ShouldReturnTokenWithCorrectClaims() {
        long tokenVersion = 1;
        Tokens.Jwt tokenType = Tokens.Jwt.ACCESS_TOKEN;
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(1, ChronoUnit.HOURS); // Expires in 1 hour

        String token = jwtGenerator.generate(user, algorithm, tokenType, tokenVersion, issuedAt, expiresAt);
        DecodedJWT decodedJWT = JWT.decode(token);

        Duration offset = Duration.ofSeconds(1);

        assertEquals(tokenType.getType(), decodedJWT.getClaim("type").asString());
        assertEquals(tokenVersion, decodedJWT.getClaim("version").asLong());
        assertEquals(user.getId(), decodedJWT.getClaim("userId").asInt());
        assertEquals(user.getRole().name().toLowerCase(), decodedJWT.getClaim("role").asString());
        assertTrue(Duration.between(issuedAt, decodedJWT.getIssuedAt().toInstant()).abs().compareTo(offset) <= 0);
        assertTrue(Duration.between(expiresAt, decodedJWT.getExpiresAt().toInstant()).abs().compareTo(offset) <= 0);
    }

    @Test
    void generate_ShouldGenerateUniqueTokenIds() {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(60L);
        String token1 = jwtGenerator.generate(user, algorithm, Tokens.Jwt.ACCESS_TOKEN, 1, issuedAt, expiresAt);
        String token2 = jwtGenerator.generate(user, algorithm, Tokens.Jwt.ACCESS_TOKEN, 1, issuedAt, expiresAt);
        assertNotEquals(JWT.decode(token1).getId(), JWT.decode(token2).getId());
    }

    @Test
    void generate_ShouldSetExpirationCorrectly() {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(60L);
        String token = jwtGenerator.generate(user, algorithm, Tokens.Jwt.ACCESS_TOKEN, 1, issuedAt, expiresAt);
        DecodedJWT decodedJWT = JWT.decode(token);
        Duration duration = Duration.between(expiresAt, decodedJWT.getExpiresAt().toInstant());
        Duration offset = Duration.ofSeconds(1);
        assertTrue(duration.abs().compareTo(offset) <= 0, "The actual instant is outside the acceptable offset range from the expected instant.");
        assertTrue(decodedJWT.getExpiresAt().toInstant().isAfter(decodedJWT.getIssuedAt().toInstant()));
    }
}
