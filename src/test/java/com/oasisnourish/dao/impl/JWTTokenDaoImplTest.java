package com.oasisnourish.dao.impl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.models.JWTToken;

import redis.clients.jedis.JedisPooled;

public class JWTTokenDaoImplTest {
    // @Mock
    // private RedisConnection redisConnection;

    // @Mock
    // private JedisPooled jedisPooled;
    // @InjectMocks
    // private JWTTokenDaoImpl jwtTokenDao;
    // @BeforeEach
    // public void setUp() {
    //     MockitoAnnotations.openMocks(this);
    //     when(redisConnection.getJedis()).thenReturn(jedisPooled);
    // }
    // @Test
    // public void testSaveToken_withValidToken_savesToRedis() {
    //     JWTToken token = new JWTToken("validToken", "AUTH", 1, System.currentTimeMillis() + 60000);
    //     jwtTokenDao.saveToken(token);
    //     String key = "jwt:" + token.getToken();
    //     verify(jedisPooled).hset(key, "tokenType", token.getTokenType());
    //     verify(jedisPooled).hset(key, "tokenVersion", String.valueOf(token.getTokenVersion()));
    //     verify(jedisPooled).hset(key, "expires", String.valueOf(token.getExpires()));
    //     // Capture the TTL argument and check it falls within a 1-second range
    //     ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);
    //     verify(jedisPooled).expire(eq(key), ttlCaptor.capture());
    //     long ttlValue = ttlCaptor.getValue();
    //     assertTrue(ttlValue >= 59 && ttlValue <= 60, "Expected TTL to be within 59 to 60 seconds, but got " + ttlValue);
    // }
    // @Test
    // public void testSaveToken_withExpiredToken_doesNotSave() {
    //     JWTToken expiredToken = new JWTToken("expiredToken", "AUTH", 1, System.currentTimeMillis() - 1000);
    //     jwtTokenDao.saveToken(expiredToken);
    //     verify(jedisPooled, never()).hset(anyString(), anyString(), anyString());
    //     verify(jedisPooled, never()).expire(anyString(), anyInt());
    // }
    // @Test
    // public void testFindToken_withExistingToken_returnsToken() {
    //     String tokenKey = "jwt:existingToken";
    //     when(jedisPooled.exists(tokenKey)).thenReturn(true);
    //     when(jedisPooled.hget(tokenKey, "tokenType")).thenReturn("AUTH");
    //     when(jedisPooled.hget(tokenKey, "tokenVersion")).thenReturn("1");
    //     when(jedisPooled.hget(tokenKey, "expires")).thenReturn(String.valueOf(System.currentTimeMillis() + 60000));
    //     Optional<JWTToken> result = jwtTokenDao.findToken("existingToken");
    //     assertTrue(result.isPresent());
    //     JWTToken token = result.get();
    //     assertEquals("existingToken", token.getToken());
    //     assertEquals("AUTH", token.getTokenType());
    //     assertEquals(1, token.getTokenVersion());
    // }
    // @Test
    // public void testFindToken_withNonExistingToken_returnsEmpty() {
    //     String tokenKey = "jwt:nonExistingToken";
    //     when(jedisPooled.exists(tokenKey)).thenReturn(false);
    //     Optional<JWTToken> result = jwtTokenDao.findToken("nonExistingToken");
    //     assertTrue(result.isEmpty());
    // }
    // @Test
    // public void testDeleteToken_deletesTokenFromRedis() {
    //     jwtTokenDao.deleteToken("toBeDeletedToken");
    //     verify(jedisPooled).del("jwt:toBeDeletedToken");
    // }
}
