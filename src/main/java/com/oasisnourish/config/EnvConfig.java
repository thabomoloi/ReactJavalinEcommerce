package com.oasisnourish.config;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Configuration class for loading environment variables using the Dotenv
 * library. This class provides a static method to access the loaded environment
 * variables.
 */
public class EnvConfig {
    private static final Dotenv dotenv;

    // Static block to load environment variables from a .env file
    static {
        dotenv = Dotenv.load();
    }

    /**
     * Retrieves the Dotenv instance containing the loaded environment variables.
     *
     * @return a {@link Dotenv} instance that provides access to the environment
     *         variables defined in the .env file.
     */
    public static Dotenv getDotenv() {
        return dotenv;
    }
}
