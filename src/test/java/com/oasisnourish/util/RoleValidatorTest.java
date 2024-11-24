package com.oasisnourish.util;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.enums.Role;
import com.oasisnourish.services.tokens.JWTService;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleValidatorTest {

    @Mock
    private Context ctx;

    @Mock
    private JWTService jwtService;

    @Mock
    private DecodedJWT jwt;

    @Mock
    private Claim claim;

    @InjectMocks
    private RoleValidator roleValidator;

    @Test
    public void testValidateRole_WithValidUserRole_Success() {
        when(jwt.getClaim("role")).thenReturn(claim);
        when(claim.asString()).thenReturn("user");
        when(ctx.routeRoles()).thenReturn(Set.of(Role.USER, Role.ADMIN));

        assertDoesNotThrow(() -> roleValidator.validateRole(ctx, jwtService, jwt));
    }

    @Test
    public void testValidateRole_WithUnrecognizedRole_DefaultsToGuest() {
        when(jwt.getClaim("role")).thenReturn(claim);
        when(claim.asString()).thenReturn("unknown_role");
        when(ctx.routeRoles()).thenReturn(Set.of(Role.USER, Role.ADMIN));

        UnauthorizedResponse exception = assertThrows(UnauthorizedResponse.class, ()
                -> roleValidator.validateRole(ctx, jwtService, jwt)
        );
        assertEquals("You are not allowed to access this route.", exception.getMessage());
    }

    @Test
    public void testValidateRole_WithNoRoleInJwt_DefaultsToGuest() {
        jwt = null;
        when(ctx.routeRoles()).thenReturn(Set.of(Role.ADMIN));

        UnauthorizedResponse exception = assertThrows(UnauthorizedResponse.class, ()
                -> roleValidator.validateRole(ctx, jwtService, jwt)
        );
        assertEquals("You are not allowed to access this route.", exception.getMessage());
    }

    @Test
    public void testValidateRole_WithNoPermittedRoles_Success() {
        when(jwt.getClaim("role")).thenReturn(claim);
        when(claim.asString()).thenReturn("guest");
        when(ctx.routeRoles()).thenReturn(Set.of());

        assertDoesNotThrow(() -> roleValidator.validateRole(ctx, jwtService, jwt));
    }

    @Test
    public void testValidateRole_WithAdminRole_Success() {
        when(jwt.getClaim("role")).thenReturn(claim);
        when(claim.asString()).thenReturn("admin");
        when(ctx.routeRoles()).thenReturn(Set.of(Role.ADMIN));

        assertDoesNotThrow(() -> roleValidator.validateRole(ctx, jwtService, jwt));
    }
}
