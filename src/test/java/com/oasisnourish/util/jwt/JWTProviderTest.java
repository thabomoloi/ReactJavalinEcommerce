package com.oasisnourish.util.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.config.JWTConfig;
import com.oasisnourish.enums.Role;
import com.oasisnourish.models.JsonWebToken;
import com.oasisnourish.models.User;

@ExtendWith(MockitoExtension.class)
public class JWTProviderTest {

    @Mock
    private Algorithm algorithm;

    @Mock
    private JWTGenerator jwtGenerator;

    @Mock
    private JWTVerifier jwtVerifier;

    @Mock
    private JWTConfig jwtConfig;

    @InjectMocks
    private JWTProvider jwtProvider;

    private final User user = new User(1, "John Doe", "john.doe@test.com", "encodedPassword", Role.ADMIN);

    @Test
    void testGenerateToken_ValidExpiry() {

        when(jwtConfig.getJwtAccessTokenExpires()).thenReturn(1); // 1 seconds
        when(jwtConfig.getJwtRefreshTokenExpires()).thenReturn(2); // 2 seconds
        when(jwtConfig.getJwtTokenMaxExpires()).thenReturn(4); // 3 seconds
        when(jwtGenerator.generate(eq(user), eq(algorithm), eq("access"), eq(1L), any(Instant.class), any(Instant.class))).thenReturn("dummy-access-token");
        when(jwtGenerator.generate(eq(user), eq(algorithm), eq("refresh"), eq(1L), any(Instant.class), any(Instant.class))).thenReturn("dummy-refresh-token");

        jwtProvider.updateJwtCurrentTime();
        jwtProvider.updateJwtMaxExpiryTime();

        JsonWebToken accessToken = jwtProvider.generateToken(user, "access", 1);
        assertNotNull(accessToken);
        assertEquals("access", accessToken.getTokenType());
        assertEquals(1, accessToken.getTokenVersion());
        assertTrue(accessToken.getExpires().isAfter(jwtProvider.getJwtCurrentTime()));
        assertTrue(accessToken.getExpires().isBefore(jwtProvider.getJwtMaxExpiryTime()));

        JsonWebToken refreshToken = jwtProvider.generateToken(user, "refresh", 1);
        assertNotNull(refreshToken);
        assertEquals("refresh", refreshToken.getTokenType());
        assertEquals(1, refreshToken.getTokenVersion());
        assertTrue(refreshToken.getExpires().isAfter(jwtProvider.getJwtCurrentTime()));
        assertTrue(refreshToken.getExpires().isBefore(jwtProvider.getJwtMaxExpiryTime()));
    }

    @Test
    void testGenerateToken_DoesNotExceedMaxExpiry() {
        when(jwtConfig.getJwtAccessTokenExpires()).thenReturn(3); // 3 seconds
        when(jwtConfig.getJwtRefreshTokenExpires()).thenReturn(3); // 3 seconds
        when(jwtConfig.getJwtTokenMaxExpires()).thenReturn(2); // 2 seconds
        when(jwtGenerator.generate(eq(user), eq(algorithm), eq("access"), eq(1L), any(Instant.class), any(Instant.class))).thenReturn("dummy-access-token");
        when(jwtGenerator.generate(eq(user), eq(algorithm), eq("refresh"), eq(1L), any(Instant.class), any(Instant.class))).thenReturn("dummy-refresh-token");

        jwtProvider.updateJwtCurrentTime();
        jwtProvider.updateJwtMaxExpiryTime();

        JsonWebToken accessToken = jwtProvider.generateToken(user, "access", 1);
        assertEquals(accessToken.getExpires(), jwtProvider.getJwtMaxExpiryTime());

        JsonWebToken refreshToken = jwtProvider.generateToken(user, "refresh", 1);
        assertEquals(refreshToken.getExpires(), jwtProvider.getJwtMaxExpiryTime());
    }

    @Test
    void validateToken_ShouldReturnToken_WhenValid() {
        String validToken = "valid-token";
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(jwtVerifier.verify(validToken)).thenReturn(decodedJWT);

        Optional<DecodedJWT> result = jwtProvider.validateToken(validToken);

        assertTrue(result.isPresent());
        assertEquals(decodedJWT, result.get());
    }

    @Test
    void validateToken_ShouldReturnEmpty_WhenInvalid() {
        String invalidToken = "invalid-token";
        when(jwtVerifier.verify(invalidToken)).thenThrow(new JWTVerificationException("Invalid token"));

        Optional<DecodedJWT> result = jwtProvider.validateToken(invalidToken);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateJwtCurrentTime_ShouldUpdateCurrentTime() {
        Instant originalTime = jwtProvider.getJwtCurrentTime();

        jwtProvider.updateJwtCurrentTime();

        assertNotEquals(originalTime, jwtProvider.getJwtCurrentTime());
    }

    @Test
    void updateJwtMaxExpiryTime_ShouldUpdateMaxExpiryTime() {
        when(jwtConfig.getJwtTokenMaxExpires()).thenReturn(2);
        Instant originalExpiryTime = jwtProvider.getJwtMaxExpiryTime();

        jwtProvider.updateJwtMaxExpiryTime();

        assertNotEquals(originalExpiryTime, jwtProvider.getJwtMaxExpiryTime());
    }

}
