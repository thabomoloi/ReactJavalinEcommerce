package com.oasisnourish.util.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.enums.Role;
import com.oasisnourish.models.User;

public class JWTGeneratorTest {

    private final User user = new User(1, "John Doe", "john.doe@test.com", "encodedPassword", Role.ADMIN);
    private final Algorithm algorithm = Algorithm.HMAC256("testSecret");
    private final JWTGenerator jwtGenerator = new JWTGenerator();

    // @Test
    // void generate_ShouldGenerateUniqueTokenIds() {
    //     long issuedAt = Instant.now().toEpochMilli();
    //     long expiresAt = issuedAt + 3600000;
    //     String token1 = jwtGenerator.generate(user, algorithm, "ACCESS", 1, issuedAt, expiresAt);
    //     String token2 = jwtGenerator.generate(user, algorithm, "ACCESS", 1, issuedAt, expiresAt);
    //     assertNotEquals(JWT.decode(token1).getId(), JWT.decode(token2).getId());
    // }
    // @Test
    // void generate_ShouldSetExpirationCorrectly() {
    //     Instant now = Instant.now();
    //     long issuedAt = now.toEpochMilli();
    //     long expiresAt = issuedAt + 60000; // Expires in 1 minute
    //     String token = jwtGenerator.generate(user, algorithm, "ACCESS", 1, issuedAt, expiresAt);
    //     DecodedJWT decodedJWT = JWT.decode(token);
    //     assertEquals(now.toEpochMilli(), Instant.ofEpochMilli(now.toEpochMilli()).toEpochMilli()); // working
    //     assertEquals(now.plusMillis(60000), Instant.ofEpochMilli(now.toEpochMilli() + 60000)); // not working
    //     // assertEquals(expiresAt, decodedJWT.getExpiresAt().toInstant().toEpochMilli());
    //     // assertTrue(decodedJWT.getExpiresAt().toInstant().isAfter(decodedJWT.getIssuedAt().toInstant()));
    // }
}
