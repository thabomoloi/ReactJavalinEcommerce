package com.oasisnourish.util;

import org.thymeleaf.context.Context;
import com.oasisnourish.models.User;

/**
 * A utility class to build email contexts for different types of emails.
 */
public class EmailContentBuilder {

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
        return context;
    }

    /**
     * Builds the context for a welcome email.
     *
     * @param user The user to include in the email context.
     * @return The context with user.
     */
    public Context buildWelcomeContext(User user) {
        Context context = new Context();
        context.setVariable("user", user);
        return context;
    }

}
