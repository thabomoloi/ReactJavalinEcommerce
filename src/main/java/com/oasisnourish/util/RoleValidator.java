package com.oasisnourish.util;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.oasisnourish.enums.Role;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.services.JWTService;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;

/**
 * RoleValidator
 */
public class RoleValidator {
    private final static Map<String, Role> rolesMapping = Map.of(
            "guest", Role.GUEST,
            "unverified_user", Role.UNVERIFIED_USER,
            "user", Role.USER,
            "admin", Role.ADMIN);

    public void validateRole(Context ctx, JWTService jwtService, DecodedJWT jwt) {
        String userRole = Optional.ofNullable(jwt)
                .map(token -> token.getClaim("role").asString().toLowerCase())
                .orElse("guest");

        Role role = rolesMapping.getOrDefault(userRole, Role.GUEST);
        Set<RouteRole> permittedRoles = ctx.routeRoles();

        if (!permittedRoles.isEmpty() && !permittedRoles.contains(role)) {
            throw new UnauthorizedResponse("You are not allowed to access this route");
        }
    }
}