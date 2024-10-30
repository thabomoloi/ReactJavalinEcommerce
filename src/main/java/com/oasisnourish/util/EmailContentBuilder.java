package com.oasisnourish.util;

import org.thymeleaf.context.Context;

import com.oasisnourish.config.EnvConfig;
import com.oasisnourish.models.User;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * A utility class to build email contexts for different types of emails.
 */
public class EmailContentBuilder {
    private final Dotenv dotenv = EnvConfig.getDotenv();

    /**
     * Builds the context for a confirmation email.
     *
     * @param user  The user to include in the email context.
     * @param token The confirmation token.
     * @return The context with user and token information.
     */
    public Context buildConfirmationContext(User user, String token) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("token", token);
        context.setVariable("baseUrl", dotenv.get("BASE_URL", "http://localhost:7070"));
        return context;
    }

}
