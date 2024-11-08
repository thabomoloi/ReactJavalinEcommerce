package com.oasisnourish.services.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.models.User;
import com.oasisnourish.services.JWTService;

import io.github.cdimascio.dotenv.Dotenv;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;

public class JWTServiceImpl implements JWTService {

    private final String SECRET_KEY;
    private final int ACCESS_TOKEN_EXPIRES;
    private final int REFRESH_TOKEN_EXPIRES;
    private final int REQUIRE_FRESH_SIGN_IN;
    private final JWTProvider<User> provider;
    private final RedisConnection redisConnection;
    private String tokenType = "access";
    private long tokenVersion;
    private LocalDateTime freshSignInTime;

    public JWTServiceImpl(RedisConnection redisConnection, Dotenv dotenv) {
        this.redisConnection = redisConnection;
        String secretKey = dotenv.get("JWT_SECRET");
        String accessTokenExpires = dotenv.get("JWT_ACCESS_TOKEN_EXPIRES");
        String refreshTokenExpires = dotenv.get("JWT_REFRESH_TOKEN_EXPIRES");
        String requireFreshSignIn = dotenv.get("JWT_REQUIRE_FRESH_SIGN_IN");

        if (secretKey == null || secretKey.isBlank() || accessTokenExpires == null || refreshTokenExpires == null
                || requireFreshSignIn == null) {
            throw new IllegalStateException("Missing JWT environment variables");
        }

        SECRET_KEY = secretKey;
        ACCESS_TOKEN_EXPIRES = Integer.parseInt(accessTokenExpires);
        REFRESH_TOKEN_EXPIRES = Integer.parseInt(refreshTokenExpires);
        REQUIRE_FRESH_SIGN_IN = Integer.parseInt(requireFreshSignIn);

        // Calculate the time after which a fresh sign-in is required
        freshSignInTime = LocalDateTime.now().plusSeconds(REQUIRE_FRESH_SIGN_IN);

        JWTGenerator<User> generator = (user, alg) -> {
            LocalDateTime currentTime = LocalDateTime.now();

            // Calculate the token expiry time based on the token type (refresh or access)
            long tokenExpiryDurationInSeconds = tokenType.equals("refresh") ? REFRESH_TOKEN_EXPIRES
                    : ACCESS_TOKEN_EXPIRES;
            LocalDateTime tokenExpiryTime = currentTime.plusSeconds(tokenExpiryDurationInSeconds);

            // Ensure the token expiry time doesn't exceed the fresh sign-in time
            if (tokenExpiryTime.isAfter(freshSignInTime)) {
                tokenExpiryTime = freshSignInTime;
            }

            JWTCreator.Builder token = JWT.create()
                    .withJWTId(UUID.randomUUID().toString())
                    .withIssuedAt(currentTime.atZone(ZoneId.systemDefault()).toInstant())
                    .withExpiresAt(tokenExpiryTime.atZone(ZoneId.systemDefault()).toInstant())
                    .withClaim("version", tokenVersion)
                    .withClaim("type", tokenType)
                    .withClaim("userId", user.getId())
                    .withClaim("role", user.getRole().name().toLowerCase());
            return token.sign(alg);
        };

        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();

        provider = new JWTProvider<>(algorithm, generator, verifier);
    }

    @Override
    public Map<String, String> generateTokens(User user) {
        var jedis = redisConnection.getJedis();
        Map<String, String> map = new HashMap<>();

        String versionKey = String.format("user:%d:tokenVersion", user.getId());
        if (!jedis.exists(versionKey)) {
            jedis.set(versionKey, "0");
        }

        tokenVersion = jedis.incr(versionKey);

        tokenType = "access";
        String accessToken = provider.generateToken(user);
        tokenType = "refresh";
        String refreshToken = provider.generateToken(user);

        saveTokenToRedis(accessToken, user.getId(), ACCESS_TOKEN_EXPIRES);
        saveTokenToRedis(refreshToken, user.getId(), REFRESH_TOKEN_EXPIRES);

        map.put("JWTAccessToken", accessToken);
        map.put("JWTRefreshToken", refreshToken);
        return map;
    }

    private void saveTokenToRedis(String token, int userId, int expires) {
        var jedis = redisConnection.getJedis();
        jedis.set(token, Integer.toString(userId));
        jedis.expire(token, expires);
    }

    @Override
    public boolean isTokenValid(String token) {
        var jedis = redisConnection.getJedis();
        return jedis.exists(token);
    }

    @Override
    public void revokeToken(String token) {
        var jedis = redisConnection.getJedis();
        jedis.del(token);
    }

    @Override
    public Optional<DecodedJWT> getToken(String token) {
        var jedis = redisConnection.getJedis();
        if (jedis.exists(token)) {
            return provider.validateToken(token);
        }
        return Optional.empty();
    }

    @Override
    public void deleteToken(String token) {
        var jedis = redisConnection.getJedis();
        if (jedis.exists(token)) {
            jedis.del(token);
        }
    }

    @Override
    public JWTProvider<User> getProvider() {
        return provider;
    }

    @Override
    public long getTokenVersion(int userId) {
        var jedis = redisConnection.getJedis();
        String versionKey = String.format("user:%d:tokenVersion", userId);
        if (!jedis.exists(versionKey)) {
            jedis.set(versionKey, "0");
        }
        return Long.parseLong(jedis.get(versionKey));
    }

    @Override
    public int getTokenExpires(String tokenType) {
        return "refresh".equals(tokenType) ? REFRESH_TOKEN_EXPIRES : ACCESS_TOKEN_EXPIRES;
    }

    @Override
    public void updateFreshSignInTime() {
        freshSignInTime = LocalDateTime.now().plusSeconds(REQUIRE_FRESH_SIGN_IN);
    }
}