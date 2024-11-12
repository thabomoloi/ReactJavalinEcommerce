package com.oasisnourish.services;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.dao.TokenDao;
import com.oasisnourish.dao.TokenVersionDao;
import com.oasisnourish.enums.Role;
import com.oasisnourish.models.JsonWebToken;
import com.oasisnourish.models.User;
import com.oasisnourish.services.impl.JWTServiceImpl;
import com.oasisnourish.util.jwt.JWTProvider;

@ExtendWith(MockitoExtension.class)
public class JWTServiceImplTest {

    @Mock
    private TokenDao<JsonWebToken> tokenDao;

    @Mock
    private TokenVersionDao tokenVersionDao;

    @Mock
    private JWTProvider provider;

    @InjectMocks
    private JWTServiceImpl jwtService;

    private final User user = new User(1, "John Doe", "john.doe@test.com", "password123", Role.USER);

    private final String accessToken = UUID.randomUUID().toString();
    private final String refreshToken = UUID.randomUUID().toString();
    private final long tokenVersion = 1L;
    private final JsonWebToken jwtAccessToken = new JsonWebToken(accessToken, "access", tokenVersion, System.currentTimeMillis() + 30000L, user.getId());
    private final JsonWebToken jwtRefreshToken = new JsonWebToken(refreshToken, "refresh", tokenVersion, System.currentTimeMillis() + 60000L, user.getId());

    @Test
    public void findToken_Exists() {
        when(tokenDao.findToken(accessToken)).thenReturn(Optional.of(jwtAccessToken));

        Optional<JsonWebToken> result = jwtService.findToken(accessToken);

        verify(tokenDao, times(1)).findToken(accessToken);
        assertTrue(result.isPresent());
        assertTrue(result.get().equals(jwtAccessToken));
    }

    @Test
    public void findToken_DoesNotExists() {
        when(tokenDao.findToken(refreshToken)).thenReturn(Optional.empty());

        Optional<JsonWebToken> result = jwtService.findToken(refreshToken);

        verify(tokenDao, times(1)).findToken(refreshToken);
        assertTrue(result.isEmpty());
    }

    @Test
    public void createToken_Success() {
        when(tokenVersionDao.increment(user.getId(), "jwt", "access")).thenReturn(tokenVersion);
        when(provider.generateToken(user, "access", tokenVersion)).thenReturn(jwtAccessToken);
        when(tokenVersionDao.increment(user.getId(), "jwt", "refresh")).thenReturn(tokenVersion);
        when(provider.generateToken(user, "refresh", tokenVersion)).thenReturn(jwtRefreshToken);

        Map<String, JsonWebToken> tokens = jwtService.createTokens(user);

        verify(tokenDao).saveToken(jwtAccessToken);
        verify(tokenDao).saveToken(jwtRefreshToken);
        assertEquals(2, tokens.size());
        assertEquals(jwtAccessToken, tokens.get("JWT_ACCESS_TOKEN"));
        assertEquals(jwtRefreshToken, tokens.get("JWT_REFRESH_TOKEN"));

    }

    @Test
    public void deleteToken() {
        jwtService.deleteToken(accessToken);

        verify(tokenDao, times(1)).deleteToken(accessToken);
    }

    public void testDecodeToken_ValidToken() {
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(tokenDao.findToken(accessToken)).thenReturn(Optional.of(jwtAccessToken));
        when(provider.validateToken(accessToken)).thenReturn(Optional.of(decodedJWT));

        Optional<DecodedJWT> result = jwtService.decodeToken(accessToken);

        assertTrue(result.isPresent());
        assertEquals(decodedJWT, result.get());
    }

    @Test
    public void testDecodeToken_InvalidToken() {
        String token = "invalidToken";

        when(tokenDao.findToken(token)).thenReturn(Optional.empty());

        Optional<DecodedJWT> result = jwtService.decodeToken(token);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetCurrentTokenVersion() {
        String tokenType = "access";

        when(tokenVersionDao.find(user.getId(), "jwt", tokenType)).thenReturn(tokenVersion);

        long result = jwtService.getCurrentTokenVersion(user.getId(), tokenType);

        assertEquals(tokenVersion, result);
    }
}
