package com.oasisnourish.util;

import java.util.Map;
import java.util.Optional;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.config.EnvConfig;
import com.oasisnourish.dto.UserResponseDto;
import com.oasisnourish.models.User;
import com.oasisnourish.services.JWTService;
import com.oasisnourish.services.UserService;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.UnauthorizedResponse;

public class SessionManager {
    private static final String JWT_ACCESS_KEY = "JWTAccessToken";
    private static final String JWT_REFRESH_KEY = "JWTRefreshToken";
    private static final Dotenv dotenv = EnvConfig.getDotenv();

    public void decodeJWTFromCookie(Context ctx, JWTService jwtService) {
        Optional.ofNullable(ctx.cookie(JWT_ACCESS_KEY))
                .flatMap(jwtService::getToken)
                .ifPresent(jwt -> ctx.sessionAttribute(JWT_ACCESS_KEY, jwt));

        Optional.ofNullable(ctx.cookie(JWT_REFRESH_KEY))
                .flatMap(jwtService::getToken)
                .ifPresent(jwt -> ctx.sessionAttribute(JWT_REFRESH_KEY, jwt));
    }

    public DecodedJWT getJwtFromSession(Context ctx) {
        return ctx.sessionAttribute(JWT_ACCESS_KEY);
    }

    public void validateAndSetUserSession(Context ctx, JWTService jwtService, UserService userService) {
        DecodedJWT jwt = ctx.sessionAttribute(JWT_ACCESS_KEY);
        if (jwt == null)
            return;

        int userId = jwt.getClaim("userId").asInt();
        long version = jwt.getClaim("version").asLong();

        if (version != jwtService.getTokenVersion(userId)) {
            invalidateSession(ctx, jwtService);
            throw new UnauthorizedResponse();
        }

        User user = ctx.sessionAttribute("currentUser");
        if (user == null || user.getId() != userId) {
            userService.findUserById(userId).ifPresentOrElse(u -> {
                ctx.sessionAttribute("currentUser", u);
            }, () -> {
                throw new UnauthorizedResponse();
            });
        }
    }

    public void invalidateSession(Context ctx, JWTService jwtService) {
        ctx.sessionAttribute("currentUser", null);
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
        if (jwt == null)
            return;

        int userId = jwt.getClaim("userId").asInt();
        long version = jwt.getClaim("version").asLong();

        User user = ctx.sessionAttribute("currentUser");

        if (version != jwtService.getTokenVersion(userId) || user == null) {
            throw new UnauthorizedResponse();
        }

        Map<String, String> tokens = jwtService.generateTokens(user);
        setTokensInCookies(ctx, tokens, jwtService);
        ctx.status(200).result("Tokens refresh successful.");
    }

    public void setTokensInCookies(Context ctx, Map<String, String> tokens, JWTService jwtService) {
        ctx.cookie(createTokenCookie("access", tokens.get(JWT_ACCESS_KEY), jwtService));
        ctx.cookie(createTokenCookie("refresh", tokens.get(JWT_REFRESH_KEY), jwtService));
    }

    private Cookie createTokenCookie(String tokenType, String token, JWTService jwtService) {
        String environment = dotenv.get("ENV", "development");
        Cookie cookie = new Cookie("access".equals(tokenType) ? JWT_ACCESS_KEY : JWT_REFRESH_KEY, token);
        cookie.setHttpOnly(true);
        cookie.setSecure("development".equals(environment));
        cookie.setSameSite(io.javalin.http.SameSite.STRICT);
        cookie.setMaxAge(jwtService.getTokenExpires(tokenType) * 60);
        return cookie;
    }
}
