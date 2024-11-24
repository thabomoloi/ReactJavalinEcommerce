package com.oasisnourish.dao.impl.tokens;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.enums.Tokens;
import com.oasisnourish.models.tokens.AuthToken;
import com.oasisnourish.models.tokens.Token;

import redis.clients.jedis.JedisPooled;

@ExtendWith(MockitoExtension.class)
public class TokenDaoImplTest {

    @Mock
    private RedisConnection redisConnection;

    @Mock
    private JedisPooled jedis;

    private TokenDaoImpl<AuthToken> authTokenDao;

    @BeforeEach
    public void setUp() {
        lenient().when(redisConnection.getJedis()).thenReturn(jedis);

        authTokenDao = new TokenDaoImpl<>(redisConnection, AuthToken.class);
    }

    @Test
    public void testSaveToken() {
        AuthToken authToken = new AuthToken("testToken", Tokens.Auth.PASSWORD_RESET_TOKEN, 1L, Instant.now().plusSeconds(60L), 1);
        String key = "token:" + authToken.getToken();

        authTokenDao.saveToken(authToken);
        verifyTokenFields(key, authToken);

        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);
        verify(jedis).expire(eq(key), ttlCaptor.capture());
        long ttlValue = ttlCaptor.getValue();
        assertTrue(ttlValue >= 59 && ttlValue <= 60, "Expected TTL to be within 59 to 60 seconds, but got " + ttlValue);
    }

    @Test
    void testSaveToken_TokenExpired() {
        AuthToken authToken = new AuthToken("expiredToken", Tokens.Auth.PASSWORD_RESET_TOKEN, 1L, Instant.now().minusSeconds(10L), 1);

        authTokenDao.saveToken(authToken);

        verify(jedis, never()).hset(anyString(), anyString(), anyString());
        verify(jedis, never()).expire(anyString(), anyInt());
    }

    @Test
    void testFindToken_AuthTokenExists() {
        String token = "testToken";
        int userId = 1;
        String key = "token:" + token;

        AuthToken expectedToken = new AuthToken(token, Tokens.Auth.ACCOUNT_CONFIRMATION_TOKEN, 1, Instant.now().plusSeconds(60L), userId);
        when(jedis.exists(key)).thenReturn(true);
        mockRedisTokenFields(key, expectedToken);

        Optional<AuthToken> result = authTokenDao.findToken(token);

        assertTrue(result.isPresent());
        assertEquals(AuthToken.class, result.get().getClass());
        AuthToken actualToken = (AuthToken) result.get();
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void testFindToken_TokenDoesNotExist() {
        String token = "nonexistentToken";
        String key = "token:" + token;

        when(jedis.exists(key)).thenReturn(false);

        Optional<AuthToken> result = authTokenDao.findToken(token);

        assertTrue(result.isEmpty());
        verify(jedis, never()).hget(anyString(), anyString());
    }

    @Test
    void testDeleteToken() {
        String token = "deleteToken";
        String key = "token:" + token;

        authTokenDao.deleteToken(token);

        verify(jedis).del(key);
    }

    @Test
    public void testFindAllTokens() {
        int userId = 1;
        String pattern = "token:*";

        Token token1 = new AuthToken("token1", Tokens.Auth.ACCOUNT_CONFIRMATION_TOKEN, 1L, Instant.now().plusSeconds(60L), userId);
        Token token2 = new AuthToken("token2", Tokens.Auth.PASSWORD_RESET_TOKEN, 1L, Instant.now().plusSeconds(90L), userId);

        Set<String> keys = new HashSet<>();
        keys.add("token:" + token1.getToken());
        keys.add("token:" + token2.getToken());

        when(jedis.keys(pattern)).thenReturn(keys);
        mockRedisTokenFields("token:" + token1.getToken(), token1);
        mockRedisTokenFields("token:" + token2.getToken(), token2);

        List<AuthToken> tokens = authTokenDao.findTokensByUserId(userId);

        assertEquals(Arrays.asList(token1, token2), tokens);
    }

    private void verifyTokenFields(String key, Token token) {
        verify(jedis).hset(key, "token", token.getToken());
        verify(jedis).hset(key, "tokenCategory", token.getTokenCategory().getCategory());
        verify(jedis).hset(key, "tokenType", token.getTokenType().getType());
        verify(jedis).hset(key, "tokenVersion", String.valueOf(token.getTokenVersion()));
        verify(jedis).hset(key, "expires", String.valueOf(token.getExpires()));
        verify(jedis).hset(key, "userId", String.valueOf(token.getUserId()));
    }

    private void mockRedisTokenFields(String key, Token token) {
        when(jedis.hget(key, "token")).thenReturn(token.getToken());
        when(jedis.hget(key, "tokenType")).thenReturn(token.getTokenType().getType());
        when(jedis.hget(key, "tokenVersion")).thenReturn(String.valueOf(token.getTokenVersion()));
        when(jedis.hget(key, "expires")).thenReturn(String.valueOf(token.getExpires()));
        when(jedis.hget(key, "userId")).thenReturn(String.valueOf(token.getUserId()));
        when(jedis.hget(key, "tokenCategory")).thenReturn(token.getTokenCategory().getCategory());
    }

}
