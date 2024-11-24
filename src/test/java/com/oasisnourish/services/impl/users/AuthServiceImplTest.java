package com.oasisnourish.services.impl.users;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.context.IContext;

import com.oasisnourish.dto.users.UserInputDto;
import com.oasisnourish.enums.Tokens;
import com.oasisnourish.exceptions.InvalidTokenException;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.tokens.AuthToken;
import com.oasisnourish.models.tokens.JsonWebToken;
import com.oasisnourish.models.users.User;
import com.oasisnourish.services.EmailService;
import com.oasisnourish.services.tokens.AuthTokenService;
import com.oasisnourish.services.tokens.JWTService;
import com.oasisnourish.services.users.UserService;
import com.oasisnourish.util.EmailContentBuilder;

import io.javalin.http.UnauthorizedResponse;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private EmailService emailService;
    @Mock
    private AuthTokenService authTokenService;
    @Mock
    private JWTService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailContentBuilder emailContentBuilder;

    @InjectMocks
    private AuthServiceImpl authService;

    private final UserInputDto userDto = new UserInputDto(1, "John Doe", "john.doe@test.com", "plainPassword");
    private final User user = new User(1, userDto.getName(), userDto.getEmail(), "encodedPassword");

    @Test
    public void testSignUpUser() {
        AuthToken token = mock(AuthToken.class);
        IContext context = mock(IContext.class);

        when(userService.findUserByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        when(authTokenService.createToken(user.getId(), Tokens.Auth.ACCOUNT_CONFIRMATION_TOKEN)).thenReturn(token);
        when(emailContentBuilder.buildEmailTokenContext(user, token)).thenReturn(context);

        authService.signUpUser(userDto);

        verify(userService).createUser(userDto);
        verify(emailService).sendEmail(eq(userDto.getEmail()), eq("Welcome to Oasis Nourish"), anyString(), eq(context));
    }

    @Test
    public void testSignInUser_Success() {
        JsonWebToken accessToken = new JsonWebToken("accessToken", Tokens.Jwt.ACCESS_TOKEN, 1, Instant.now().plusSeconds(30L), user.getId());
        JsonWebToken refreshToken = new JsonWebToken("refreshToken", Tokens.Jwt.REFRESH_TOKEN, 1, Instant.now().plusSeconds(60L), user.getId());

        when(userService.findUserByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.createTokens(user)).thenReturn(Map.of("JWT_ACCESS_TOKEN", accessToken, "JWT_REFRESH_TOKEN", refreshToken));

        Map<String, JsonWebToken> tokens = authService.signInUser(userDto);

        assertNotNull(tokens);
        assertEquals(accessToken, tokens.get("JWT_ACCESS_TOKEN"));
        assertEquals(refreshToken, tokens.get("JWT_REFRESH_TOKEN"));
    }

    @Test
    public void testSignInUser_InvalidCredentials() {
        when(userService.findUserByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userDto.getPassword(), user.getPassword())).thenReturn(false);

        UnauthorizedResponse exception = assertThrows(UnauthorizedResponse.class, () -> authService.signInUser(userDto));
        assertEquals("Invalid email or password.", exception.getMessage());
    }

    @Test
    public void testSendConfirmationToken_UserExists() {
        AuthToken authToken = new AuthToken("token", Tokens.Auth.ACCOUNT_CONFIRMATION_TOKEN, 1, Instant.now().plusSeconds(10L), 1);
        IContext context = mock(IContext.class);

        when(userService.findUserById(user.getId())).thenReturn(Optional.of(user));
        when(authTokenService.createToken(user.getId(), Tokens.Auth.ACCOUNT_CONFIRMATION_TOKEN)).thenReturn(authToken);
        when(emailContentBuilder.buildEmailTokenContext(user, authToken)).thenReturn(context);

        authService.sendConfirmationToken(user.getId());

        verify(emailService).sendEmail(eq(user.getEmail()), eq("Confirm Your Email Address"), eq("user/confirm"), any(IContext.class));
    }

    @Test
    public void testSendConfirmationToken_UserNotFound() {
        when(userService.findUserById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> authService.sendConfirmationToken(user.getId()));
        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void testConfirmAccount_ValidToken() {
        AuthToken authToken = new AuthToken("validToken", Tokens.Auth.ACCOUNT_CONFIRMATION_TOKEN, 1, Instant.now().plusSeconds(10L), user.getId());

        when(authTokenService.findToken("validToken")).thenReturn(Optional.of(authToken));
        when(userService.findUserById(user.getId())).thenReturn(Optional.of(user));

        authService.confirmAccount("validToken");

        verify(userService).verifyEmail(user.getEmail());
        verify(authTokenService).deleteToken(authToken.getToken());
    }

    @Test
    public void testConfirmAccount_InvalidToken() {
        when(authTokenService.findToken("invalidToken")).thenReturn(Optional.empty());

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () -> authService.confirmAccount("invalidToken"));
        assertEquals("The authentication token is either invalid or has expired. Please request a new one.", exception.getMessage());
    }

    @Test
    public void testSendResetPasswordToken_UserExists() {
        IContext context = mock(IContext.class);
        AuthToken authToken = new AuthToken("token", Tokens.Auth.PASSWORD_RESET_TOKEN, 1, Instant.now().plusSeconds(10L), user.getId());

        when(userService.findUserByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        when(authTokenService.createToken(1, Tokens.Auth.PASSWORD_RESET_TOKEN)).thenReturn(authToken);
        when(emailContentBuilder.buildEmailTokenContext(user, authToken)).thenReturn(context);

        authService.sendResetPasswordToken(userDto.getEmail());

        verify(emailService).sendEmail(eq(userDto.getEmail()), eq("Reset your password"), eq("user/reset-password"), any(IContext.class));
    }

    @Test
    public void testSendResetPasswordToken_UserNotFound() {
        when(userService.findUserByEmail(userDto.getEmail())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> authService.sendResetPasswordToken(userDto.getEmail()));
        assertEquals("User does not exist.", exception.getMessage());

    }

    @Test
    public void testResetPassword_ValidToken() {
        AuthToken authToken = new AuthToken("resetToken", Tokens.Auth.ACCOUNT_CONFIRMATION_TOKEN, 1, Instant.now().plusSeconds(10L), user.getId());

        when(authTokenService.findToken("resetToken")).thenReturn(Optional.of(authToken));
        when(userService.findUserById(1)).thenReturn(Optional.of(user));

        authService.resetPassword("resetToken", "newPassword");

        verify(userService).updatePassword(user.getId(), "newPassword");
        verify(authTokenService).deleteToken(authToken.getToken());
    }

    @Test
    public void testResetPassword_InvalidToken() {
        when(authTokenService.findToken("invalidToken")).thenReturn(Optional.empty());

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () -> authService.resetPassword("invalidToken", "newPassword"));
        assertEquals("The authentication token is either invalid or has expired. Please request a new one.", exception.getMessage());
    }

}
