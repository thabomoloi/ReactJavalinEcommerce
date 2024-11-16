package com.oasisnourish.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.dto.UserResponseDto;
import com.oasisnourish.enums.Tokens;
import com.oasisnourish.models.JsonWebToken;
import com.oasisnourish.models.User;
import com.oasisnourish.services.JWTService;
import com.oasisnourish.services.UserService;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.SameSite;
import io.javalin.http.UnauthorizedResponse;

public class SessionManager {

    private static final String JWT_ACCESS_KEY = "JWT_ACCESS_TOKEN";
    private static final String JWT_REFRESH_KEY = "JWT_REFRESH_TOKEN";
    private final Dotenv dotenv;

    public SessionManager(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    public void updateJwtInSession(Map<String, JsonWebToken> tokens, Context ctx, JWTService jwtService) {
        jwtService.decodeToken(tokens.get(JWT_ACCESS_KEY).getToken()).ifPresent((jwt) -> {
            ctx.sessionAttribute(JWT_ACCESS_KEY, jwt);
        });
        jwtService.decodeToken(tokens.get(JWT_REFRESH_KEY).getToken()).ifPresent((jwt) -> {
            ctx.sessionAttribute(JWT_REFRESH_KEY, jwt);
        });
    }

    public void decodeJWTFromCookie(Context ctx, JWTService jwtService, UserService userService) {
        String access = ctx.cookie(JWT_ACCESS_KEY);
        String refresh = ctx.cookie(JWT_REFRESH_KEY);

        if (access != null) {
            Optional<DecodedJWT> decodedAccess = jwtService.decodeToken(access);
            if (decodedAccess.isPresent()) {
                ctx.sessionAttribute(JWT_ACCESS_KEY, decodedAccess.get());
            } else {
                ctx.sessionAttribute(JWT_ACCESS_KEY, null);
            }
        }

        if (refresh != null && (access == null || !jwtService.findToken(access).isPresent())) {
            Optional<DecodedJWT> decodedRefresh = jwtService.decodeToken(refresh);
            decodedRefresh.ifPresentOrElse(jwt -> {
                ctx.sessionAttribute(JWT_REFRESH_KEY, jwt);
                int userId = jwt.getClaim("userId").asInt();
                Optional<User> user = userService.findUserById(userId);
                if (user.isPresent()) {
                    Map<String, JsonWebToken> newTokens = jwtService.createTokens(user.get());
                    setTokensInCookies(ctx, newTokens);
                    ctx.sessionAttribute(JWT_ACCESS_KEY, jwtService.decodeToken(newTokens.get(JWT_ACCESS_KEY).getToken()).get());
                } else {
                    invalidateSession(ctx);
                }
            }, () -> ctx.sessionAttribute(JWT_REFRESH_KEY, null));
        }
    }

    public DecodedJWT getJwtFromSession(Context ctx) {
        return ctx.sessionAttribute(JWT_ACCESS_KEY);
    }

    public void validateAndSetUserSession(Context ctx, JWTService jwtService, UserService userService) {
        DecodedJWT jwt = ctx.sessionAttribute(JWT_ACCESS_KEY);
        if (jwt == null) {
            return;
        }

        int userId = jwt.getClaim("userId").asInt();
        long version = jwt.getClaim("version").asLong();

        if (version != jwtService.getCurrentTokenVersion(userId, Tokens.Jwt.ACCESS_TOKEN)) {
            invalidateSession(ctx);
            throw new UnauthorizedResponse("Invalid token: version outdated.");
        }

        userService.findUserById(userId).ifPresentOrElse(
                user -> ctx.sessionAttribute("currentUser", user),
                () -> invalidateSession(ctx));
    }

    public void invalidateSession(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.removeCookie(JWT_ACCESS_KEY);
        ctx.removeCookie(JWT_REFRESH_KEY);
    }

    public void getCurrentUser(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        if (user != null) {
            ctx.json(UserResponseDto.fromModel(user));
        } else {
            ctx.status(404).result("No user is currently logged in.");
        }
    }

    public void refreshToken(Context ctx, JWTService jwtService) {
        DecodedJWT jwt = ctx.sessionAttribute(JWT_REFRESH_KEY);
        if (jwt == null) {
            return;
        }

        int userId = jwt.getClaim("userId").asInt();
        long version = jwt.getClaim("version").asLong();

        User user = ctx.sessionAttribute("currentUser");

        if (version != jwtService.getCurrentTokenVersion(userId, Tokens.Jwt.REFRESH_TOKEN) || user == null) {
            invalidateSession(ctx);
            throw new UnauthorizedResponse("Cannot refresh token: version outdated.");
        }

        Map<String, JsonWebToken> tokens = jwtService.createTokens(user);
        setTokensInCookies(ctx, tokens);
        ctx.status(200).result("Tokens refresh successful.");
    }

    public void setTokensInCookies(Context ctx, Map<String, JsonWebToken> tokens) {
        ctx.cookie(createTokenCookie(tokens.get(JWT_ACCESS_KEY)));
        ctx.cookie(createTokenCookie(tokens.get(JWT_REFRESH_KEY)));
    }

    private Cookie createTokenCookie(JsonWebToken token) {
        String environment = dotenv.get("ENV", "development");
        Cookie cookie = new Cookie(Tokens.Jwt.ACCESS_TOKEN == token.getTokenType() ? JWT_ACCESS_KEY : JWT_REFRESH_KEY, token.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure("production".equals(environment));
        cookie.setSameSite(SameSite.STRICT);
        cookie.setMaxAge((int) Duration.between(Instant.now(), token.getExpires()).toSeconds());
        return cookie;
    }
}
