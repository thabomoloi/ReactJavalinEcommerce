package com.oasisnourish.services.impl.tokens;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oasisnourish.config.AuthTokenConfig;
import com.oasisnourish.dao.tokens.TokenDao;
import com.oasisnourish.dao.tokens.TokenRateLimitDao;
import com.oasisnourish.dao.tokens.TokenVersionDao;
import com.oasisnourish.enums.Tokens;
import com.oasisnourish.exceptions.TooManyRequestsException;
import com.oasisnourish.models.tokens.AuthToken;

@ExtendWith(MockitoExtension.class)
public class AuthTokenServiceImplTest {

    @Mock
    private TokenDao<AuthToken> tokenDao;

    @Mock
    private TokenVersionDao tokenVersionDao;

    @Mock
    private TokenRateLimitDao tokenRateLimitDao;

    @Mock
    private AuthTokenConfig tokenConfig;

    @InjectMocks
    private AuthTokenServiceImpl authTokenService;

    private final int userId = 1;
    private final Tokens.Auth tokenType = Tokens.Auth.ACCOUNT_CONFIRMATION_TOKEN;
    private final String token = UUID.randomUUID().toString();

    @BeforeEach
    public void setUp() {
        lenient().when(tokenConfig.getMaxTokensPerWindow()).thenReturn(5);
        lenient().when(tokenConfig.getRateLimitWindow()).thenReturn(120);
        lenient().when(tokenConfig.getTokenExpires()).thenReturn(60);
    }

    @Test
    public void findToken_Exists() {
        AuthToken authToken = new AuthToken(token, tokenType, 1L, Instant.now().plusSeconds(30L), userId);
        when(tokenDao.findToken(token)).thenReturn(Optional.of(authToken));

        Optional<AuthToken> result = authTokenService.findToken(token);

        verify(tokenDao, times(1)).findToken(token);
        assertTrue(result.isPresent());
        assertTrue(result.get().equals(authToken));
    }

    @Test
    public void findToken_DoesNotExists() {
        when(tokenDao.findToken(token)).thenReturn(Optional.empty());

        Optional<AuthToken> result = authTokenService.findToken(token);

        verify(tokenDao, times(1)).findToken(token);
        assertTrue(result.isEmpty());
    }

    @Test
    public void createToken_Success() {
        when(tokenRateLimitDao.find(userId)).thenReturn(0L);
        when(tokenVersionDao.find(userId, Tokens.Category.AUTH, tokenType)).thenReturn(1L);
        when(tokenDao.findTokensByUserId(userId)).thenReturn(Collections.emptyList());

        authTokenService.createToken(userId, tokenType);

        verify(tokenDao, times(1)).saveToken(any(AuthToken.class));
        verify(tokenVersionDao, times(1)).increment(userId, Tokens.Category.AUTH, tokenType);
        verify(tokenRateLimitDao, times(1)).increment(userId, 60);

    }

    @Test
    public void createToken_ThrowsTooManyRequests() {
        when(tokenRateLimitDao.find(userId)).thenReturn(6L);
        when(tokenRateLimitDao.ttl(userId)).thenReturn(5L);
        var exception = assertThrows(TooManyRequestsException.class, () -> authTokenService.createToken(userId, tokenType));
        assertEquals("Too many requests. Try again after 5 seconds.", exception.getMessage());
    }

    @Test
    public void createToken_DeletePreviousToken() {
        AuthToken previousToken = new AuthToken(token, tokenType, 1L, Instant.now().plusSeconds(30L), userId);
        when(tokenRateLimitDao.find(userId)).thenReturn(0L);
        when(tokenVersionDao.find(userId, Tokens.Category.AUTH, tokenType)).thenReturn(1L);
        when(tokenDao.findTokensByUserId(userId)).thenReturn(Collections.singletonList(previousToken));

        authTokenService.createToken(userId, tokenType);

        verify(tokenDao, times(1)).deleteToken(token);
        verify(tokenDao, times(1)).saveToken(any(AuthToken.class));
    }

    @Test
    public void deleteToken() {
        authTokenService.deleteToken(token);

        verify(tokenDao, times(1)).deleteToken(token);
    }
}
