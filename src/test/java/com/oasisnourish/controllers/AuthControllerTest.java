package com.oasisnourish.controllers;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.models.JsonWebToken;
import com.oasisnourish.models.User;
import com.oasisnourish.services.AuthService;
import com.oasisnourish.services.JWTService;
import com.oasisnourish.services.UserService;
import com.oasisnourish.util.RoleValidator;
import com.oasisnourish.util.SessionManager;
import com.oasisnourish.util.jwt.JWTProvider;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.validation.BodyValidator;
import io.javalin.validation.Validator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private RoleValidator roleValidator;

    @Mock
    private Context ctx;

    @Mock
    private BodyValidator<UserInputDto> bodyValidator;

    @Mock
    private Validator<Integer> userIdPathParam;

    @InjectMocks
    private AuthController authController;

    private final UserInputDto userDto = new UserInputDto(1, "John Doe", "john.doe@test.com", "plainPassword");
    private final User user = new User(1, userDto.getName(), userDto.getEmail(), "encodedPassword");

    @Test
    void testSignUpUser() {
        when(ctx.bodyValidator(UserInputDto.class)).thenReturn(bodyValidator);
        when(bodyValidator.get()).thenReturn(userDto);
        authController.signUpUser(ctx);

        verify(authService).signUpUser(userDto);
        verify(ctx).status(HttpStatus.CREATED);
        verify(ctx).result("Account created. Check your email for the confirmation link.");
    }

    @Test
    void testSignInUser() {
        JsonWebToken jwtAccessToken = new JsonWebToken("accessToken", "access", 1L, System.currentTimeMillis() + 30000L, user.getId());
        JsonWebToken jwtRefreshToken = new JsonWebToken("refreshToken", "refresh", 1L, System.currentTimeMillis() + 60000L, user.getId());
        Map<String, JsonWebToken> tokens = Map.of("JWT_ACCESS_TOKEN", jwtAccessToken, "JWT_REFRESH_TOKEN", jwtRefreshToken);

        when(ctx.bodyValidator(UserInputDto.class)).thenReturn(bodyValidator);
        when(bodyValidator.get()).thenReturn(userDto);
        when(jwtService.getProvider()).thenReturn(mock(JWTProvider.class));
        when(authService.signInUser(userDto)).thenReturn(tokens);

        authController.signInUser(ctx);

        verify(authService).signInUser(userDto);
        verify(sessionManager).setTokensInCookies(ctx, tokens);
        verify(ctx).status(HttpStatus.OK);
        verify(ctx).result("Sign in successful.");
    }

    @Test
    void testGenerateConfirmationToken() {
        when(ctx.pathParamAsClass("userId", Integer.class)).thenReturn(userIdPathParam);
        when(userIdPathParam.get()).thenReturn(user.getId());
        doNothing().when(authService).sendConfirmationToken(anyInt());

        authController.generateConfirmationToken(ctx);

        verify(authService).sendConfirmationToken(anyInt());
        verify(ctx).status(HttpStatus.CREATED);
        verify(ctx).result("Confirmation token generated. Check your email for the link.");
    }

    @Test
    void testConfirmAccountToken() {
        when(ctx.pathParam("token")).thenReturn("confrimation-token");
        doNothing().when(authService).confirmAccount(anyString());

        authController.confirmAccountToken(ctx);

        verify(authService).confirmAccount("confrimation-token");
        verify(ctx).result("Your email address has been verified.");
    }

    @Test
    void testGenerateResetPasswordToken() {
        when(ctx.bodyValidator(UserInputDto.class)).thenReturn(bodyValidator);
        when(bodyValidator.get()).thenReturn(userDto);

        doNothing().when(authService).sendResetPasswordToken(userDto.getEmail());

        authController.generateResetPasswordToken(ctx);

        verify(authService).sendResetPasswordToken(anyString());
        verify(ctx).status(HttpStatus.CREATED);
        verify(ctx).result("If the email is registered, a password reset link has been sent.");
    }

    @Test
    void testResetPassword() {
        String token = "reset-password";
        when(ctx.pathParam("token")).thenReturn(token);
        when(ctx.bodyValidator(UserInputDto.class)).thenReturn(bodyValidator);
        when(bodyValidator.get()).thenReturn(userDto);
        doNothing().when(authService).resetPassword(token, userDto.getPassword());

        authController.resetPassword(ctx);

        verify(authService).resetPassword(anyString(), anyString());
        verify(ctx).status(HttpStatus.OK);
        verify(ctx).result("Your password has been reset.");
    }

    @Test
    void testSignOutUser() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        doNothing().when(sessionManager).invalidateSession(ctx);
        when(ctx.req()).thenReturn(req);
        when(req.getSession()).thenReturn(session);

        authController.signOutUser(ctx);

        verify(sessionManager).invalidateSession(ctx);
        verify(session).invalidate();
        verify(ctx).status(HttpStatus.NO_CONTENT);
        verify(ctx).result("Sign out successful.");
    }

    @Test
    void testHandle() {
        DecodedJWT decodedJwt = mock(DecodedJWT.class);
        doNothing().when(sessionManager).validateAndSetUserSession(ctx, jwtService, userService);
        when(sessionManager.getJwtFromSession(ctx)).thenReturn(decodedJwt);
        doNothing().when(roleValidator).validateRole(ctx, jwtService, decodedJwt);

        authController.handle(ctx);

        verify(sessionManager).validateAndSetUserSession(ctx, jwtService, userService);
        verify(roleValidator).validateRole(ctx, jwtService, decodedJwt);
    }

    @Test
    void testUpdateSessionUserIfChanged() {
        JsonWebToken jwtAccessToken = new JsonWebToken("accessToken", "access", 1L, System.currentTimeMillis() + 30000L, user.getId());
        JsonWebToken jwtRefreshToken = new JsonWebToken("refreshToken", "refresh", 1L, System.currentTimeMillis() + 60000L, user.getId());
        Map<String, JsonWebToken> tokens = Map.of("JWT_ACCESS_TOKEN", jwtAccessToken, "JWT_REFRESH_TOKEN", jwtRefreshToken);

        when(ctx.sessionAttribute("currentUser")).thenReturn(user);
        when(authService.updateSignedInUserIfChanged(user)).thenReturn(Optional.of(tokens));

        authController.updateSessionUserIfChanged(ctx);

        verify(sessionManager).setTokensInCookies(ctx, tokens);
        verify(sessionManager).updateJwtInSession(tokens, ctx, jwtService);
        verify(sessionManager).validateAndSetUserSession(ctx, jwtService, userService);
    }
}
