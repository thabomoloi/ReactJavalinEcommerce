package com.oasisnourish.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EmailConfig extends ConfigLoader {

    private final String mailPassword;
    private final String mailUsername;

    public EmailConfig(Dotenv dotenv) {
        super(dotenv);
        mailPassword = getEnvVar("MAIL_PASSWORD", null);
        mailUsername = getEnvVar("MAIL_USERNAME", null);
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public String getMailUsername() {
        return mailUsername;
    }

}
