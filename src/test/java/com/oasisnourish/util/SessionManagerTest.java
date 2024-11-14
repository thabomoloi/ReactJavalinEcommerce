package com.oasisnourish.util;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.enums.Role;
import com.oasisnourish.enums.Tokens;
import com.oasisnourish.models.JsonWebToken;
import com.oasisnourish.models.User;
import com.oasisnourish.services.JWTService;
import com.oasisnourish.services.UserService;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.UnauthorizedResponse;

@ExtendWith(MockitoExtension.class)
public class SessionManagerTest {

    @Mock
    private Dotenv dotenv;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private Context ctx;

    @Mock
    private DecodedJWT decodedJWT;

    @InjectMocks
    private SessionManager sessionManager;

    private final User user = new User(1, "John Doe", "john.doe@test.com", "encodedPassword", Role.ADMIN);
    private final JsonWebToken accessToken = new JsonWebToken("access-token", Tokens.Jwt.ACCESS_TOKEN, 1, Instant.now().plusSeconds(180), 1);
    private final JsonWebToken refreshToken = new JsonWebToken("refresh-token", Tokens.Jwt.REFRESH_TOKEN, 1, Instant.now().plusSeconds(360), 1);
    private final Map<String, JsonWebToken> tokens = Map.of(
            "JWT_ACCESS_TOKEN", accessToken,
            "JWT_REFRESH_TOKEN", refreshToken
    );

    @Test
    public void testUpdateJwtInSession_SetsSessionAttributes() {

        when(jwtService.decodeToken(accessToken.getToken())).thenReturn(Optional.of(decodedJWT));
        when(jwtService.decodeToken(refreshToken.getToken())).thenReturn(Optional.of(decodedJWT));

        sessionManager.updateJwtInSession(tokens, ctx, jwtService);

        verify(ctx).sessionAttribute("JWT_ACCESS_TOKEN", decodedJWT);
        verify(ctx).sessionAttribute("JWT_REFRESH_TOKEN", decodedJWT);
    }

    @Test
    public void testDecodeJWTFromCookie_WithValidAccessAndRefreshTokens() {
        when(ctx.cookie("JWT_ACCESS_TOKEN")).thenReturn("access-token");
        when(ctx.cookie("JWT_REFRESH_TOKEN")).thenReturn("refresh-token");
        when(jwtService.decodeToken("access-token")).thenReturn(Optional.of(decodedJWT));
        when(jwtService.findToken("access-token")).thenReturn(Optional.of(accessToken));

        sessionManager.decodeJWTFromCookie(ctx, jwtService, userService);

        verify(ctx).sessionAttribute("JWT_ACCESS_TOKEN", decodedJWT);
        verify(ctx, never()).sessionAttribute("JWT_REFRESH_TOKEN", decodedJWT);
    }

    @Test
    public void testDecodeJWTFromCookie_WithRefreshTokenOnly() {
        Claim claim = mock(Claim.class);

        when(ctx.cookie("JWT_ACCESS_TOKEN")).thenReturn(null);
        when(ctx.cookie("JWT_REFRESH_TOKEN")).thenReturn("refresh-token");
        when(jwtService.decodeToken("access-token")).thenReturn(Optional.of(decodedJWT));
        when(jwtService.decodeToken("refresh-token")).thenReturn(Optional.of(decodedJWT));
        when(decodedJWT.getClaim("userId")).thenReturn(claim);
        when(claim.asInt()).thenReturn(user.getId());
        when(userService.findUserById(user.getId())).thenReturn(Optional.of(user));
        when(jwtService.createTokens(user)).thenReturn(tokens);

        sessionManager.decodeJWTFromCookie(ctx, jwtService, userService);

        verify(ctx).sessionAttribute("JWT_ACCESS_TOKEN", decodedJWT);
        verify(ctx).sessionAttribute("JWT_REFRESH_TOKEN", decodedJWT);
        verify(decodedJWT).getClaim("userId");
        verify(jwtService).createTokens(user);

    }

    @Test
    public void testDecodeJWTFromCookie_WithAccessTokenNotInDB() {
        Claim claim = mock(Claim.class);

        when(ctx.cookie("JWT_ACCESS_TOKEN")).thenReturn("access-token");
        when(ctx.cookie("JWT_REFRESH_TOKEN")).thenReturn("refresh-token");
        when(jwtService.decodeToken("access-token")).thenReturn(Optional.of(decodedJWT));
        when(jwtService.decodeToken("refresh-token")).thenReturn(Optional.of(decodedJWT));
        when(decodedJWT.getClaim("userId")).thenReturn(claim);
        when(claim.asInt()).thenReturn(user.getId());
        when(jwtService.findToken("access-token")).thenReturn(Optional.empty());
        when(userService.findUserById(user.getId())).thenReturn(Optional.of(user));
        when(jwtService.createTokens(user)).thenReturn(tokens);

        sessionManager.decodeJWTFromCookie(ctx, jwtService, userService);

        verify(ctx, times(2)).sessionAttribute("JWT_ACCESS_TOKEN", decodedJWT);
        verify(ctx).sessionAttribute("JWT_REFRESH_TOKEN", decodedJWT);
        verify(decodedJWT).getClaim("userId");
        verify(jwtService).createTokens(user);
    }

    @Test
    public void testValidateAndSetUserSession_SetsUserInSession() {
        Claim userIdClaim = mock(Claim.class);
        Claim versionClaim = mock(Claim.class);

        when(ctx.sessionAttribute("JWT_ACCESS_TOKEN")).thenReturn(decodedJWT);
        when(decodedJWT.getClaim("userId")).thenReturn(userIdClaim);
        when(userIdClaim.asInt()).thenReturn(user.getId());
        when(decodedJWT.getClaim("version")).thenReturn(versionClaim);
        when(versionClaim.asLong()).thenReturn(1L);
        when(jwtService.getCurrentTokenVersion(user.getId(), Tokens.Jwt.ACCESS_TOKEN)).thenReturn(1L);
        when(userService.findUserById(user.getId())).thenReturn(Optional.of(user));

        sessionManager.validateAndSetUserSession(ctx, jwtService, userService);
        verify(ctx).sessionAttribute("currentUser", user);
    }

    @Test
    public void testValidateAndSetUserSession_ClearSessionWhenUserDeleted() {
        Claim userIdClaim = mock(Claim.class);
        Claim versionClaim = mock(Claim.class);

        when(ctx.sessionAttribute("JWT_ACCESS_TOKEN")).thenReturn(decodedJWT);
        when(decodedJWT.getClaim("userId")).thenReturn(userIdClaim);
        when(userIdClaim.asInt()).thenReturn(user.getId());
        when(decodedJWT.getClaim("version")).thenReturn(versionClaim);
        when(versionClaim.asLong()).thenReturn(1L);
        when(jwtService.getCurrentTokenVersion(user.getId(), Tokens.Jwt.ACCESS_TOKEN)).thenReturn(1L);
        when(userService.findUserById(user.getId())).thenReturn(Optional.empty());

        UnauthorizedResponse exception = assertThrows(UnauthorizedResponse.class, () -> {
            sessionManager.validateAndSetUserSession(ctx, jwtService, userService);
        });
        assertEquals("User does not exist, session cleared.", exception.getMessage());
        verify(ctx).sessionAttribute("currentUser", null);
        verify(ctx).removeCookie("JWT_ACCESS_TOKEN");
        verify(ctx).removeCookie("JWT_REFRESH_TOKEN");
    }

    @Test
    public void testValidateAndSetUserSession_WithOutdatedTokenVersion_ShouldInvalidateSession() {
        Claim userIdClaim = mock(Claim.class);
        Claim versionClaim = mock(Claim.class);

        when(ctx.sessionAttribute("JWT_ACCESS_TOKEN")).thenReturn(decodedJWT);
        when(decodedJWT.getClaim("userId")).thenReturn(userIdClaim);
        when(userIdClaim.asInt()).thenReturn(user.getId());
        when(decodedJWT.getClaim("version")).thenReturn(versionClaim);
        when(versionClaim.asLong()).thenReturn(1L);
        when(jwtService.getCurrentTokenVersion(user.getId(), Tokens.Jwt.ACCESS_TOKEN)).thenReturn(2L);

        UnauthorizedResponse exception = assertThrows(UnauthorizedResponse.class, () -> {
            sessionManager.validateAndSetUserSession(ctx, jwtService, userService);
        });
        assertEquals("Invalid token: version outdated.", exception.getMessage());
        verify(ctx).sessionAttribute("currentUser", null);
        verify(ctx).removeCookie("JWT_ACCESS_TOKEN");
        verify(ctx).removeCookie("JWT_REFRESH_TOKEN");
    }

    @Test
    public void testInvalidateSession_RemovesAttributesAndCookies() {
        sessionManager.invalidateSession(ctx);

        verify(ctx).sessionAttribute("currentUser", null);
        verify(ctx).removeCookie("JWT_ACCESS_TOKEN");
        verify(ctx).removeCookie("JWT_REFRESH_TOKEN");
    }

    @Test
    public void testSetTokensInCookies_SetsCookiesProperly() {
        sessionManager.setTokensInCookies(ctx, tokens);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(ctx, times(2)).cookie(cookieCaptor.capture());

        Cookie accessCookie = cookieCaptor.getAllValues().get(0);
        assertEquals("JWT_ACCESS_TOKEN", accessCookie.getName());
        assertEquals("access-token", accessCookie.getValue());
        assertTrue(accessCookie.isHttpOnly());
        assertFalse(accessCookie.getSecure());

        Cookie refreshCookie = cookieCaptor.getAllValues().get(1);
        assertEquals("JWT_REFRESH_TOKEN", refreshCookie.getName());
        assertEquals("refresh-token", refreshCookie.getValue());
        assertTrue(refreshCookie.isHttpOnly());
        assertFalse(refreshCookie.getSecure());
    }

    @Test
    public void testSetTokensInCookies_SetsSecureCookies() {
        when(dotenv.get("ENV", "development")).thenReturn("production");

        sessionManager.setTokensInCookies(ctx, tokens);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(ctx, times(2)).cookie(cookieCaptor.capture());

        Cookie accessCookie = cookieCaptor.getAllValues().get(0);
        assertTrue(accessCookie.getSecure());

        Cookie refreshCookie = cookieCaptor.getAllValues().get(1);
        assertTrue(refreshCookie.getSecure());
    }
}
