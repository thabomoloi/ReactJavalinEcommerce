package com.oasisnourish.util;

import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import com.oasisnourish.config.AuthTokenConfig;
import com.oasisnourish.models.Token;
import com.oasisnourish.models.User;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * A utility class to build email contexts for different types of emails.
 */
public class EmailContentBuilder {

    private final Dotenv dotenv;

    public EmailContentBuilder(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    public IContext buildEmailTokenContext(User user, Token token) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("token", token);
        context.setVariable("baseUrl", dotenv.get("BASE_URL", "http://localhost:7070"));

        context.setVariable("timeFormatter", new TimeFormatter());
        context.setVariable("authTokenConfig", new AuthTokenConfig(dotenv));
        return context;
    }

}
