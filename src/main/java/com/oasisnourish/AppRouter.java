package com.oasisnourish;

import static io.javalin.apibuilder.ApiBuilder.*;

import com.oasisnourish.enums.Role;

public class AppRouter {
    private final AppConfig CONFIG;

    public AppRouter(AppConfig config) {
        CONFIG = config;
    }

    public void initializeRoutes() {
        path("/api", () -> {
            path("/users", () -> {
                get(CONFIG.USER_CONTROLLER::findAllUsers, Role.ADMIN);
                post(CONFIG.USER_CONTROLLER::createUser, Role.ADMIN);
                path("/{userId}", () -> {
                    get(CONFIG.USER_CONTROLLER::findUserById, Role.ADMIN);
                    patch(CONFIG.USER_CONTROLLER::updateUser, Role.UNVERIFIED_USER, Role.USER, Role.ADMIN);
                    delete(CONFIG.USER_CONTROLLER::deleteUser, Role.UNVERIFIED_USER, Role.USER, Role.ADMIN);
                });
            });
            path("/auth", () -> {
                get("/me", CONFIG.AUTH_CONTROLLER::getCurrentUser, Role.UNVERIFIED_USER, Role.USER, Role.ADMIN);
                post("/signup", CONFIG.AUTH_CONTROLLER::signUpUser, Role.GUEST);
                post("/signin", CONFIG.AUTH_CONTROLLER::signInUser, Role.GUEST);
                delete("/signout", CONFIG.AUTH_CONTROLLER::signOutUser, Role.UNVERIFIED_USER, Role.USER, Role.ADMIN);
                post("/refresh", CONFIG.AUTH_CONTROLLER::refreshToken, Role.UNVERIFIED_USER, Role.USER, Role.ADMIN);
                path("/confirm/{userId}", () -> {
                    post(CONFIG.AUTH_CONTROLLER::generateConfirmationToken, Role.UNVERIFIED_USER);
                    patch("/{token}", CONFIG.AUTH_CONTROLLER::confirmAccountToken, Role.UNVERIFIED_USER);
                });
                path("/reset-password", () -> {
                    post(CONFIG.AUTH_CONTROLLER::generateResetPasswordToken, Role.GUEST);
                    patch("/{userId}/{token}", CONFIG.AUTH_CONTROLLER::resetPassword, Role.GUEST);
                });
            });
        });
    }
}
