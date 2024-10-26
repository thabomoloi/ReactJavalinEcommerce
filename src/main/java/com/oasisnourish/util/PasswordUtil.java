package com.oasisnourish.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class for handling password hashing and verification using BCrypt.
 */
public class PasswordUtil {
    private static final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    /**
     * Hashes a plaintext password using BCrypt algorithm.
     *
     * @param plainTextPassword the plaintext password to hash.
     * @return the hashed password as a String.
     */
    public static String hashPassword(String plainTextPassword) {
        return bcrypt.encode(plainTextPassword);
    }

    /**
     * Checks if a given plaintext password matches a hashed password.
     *
     * @param plainTextPassword the plaintext password to check.
     * @param hashedPassword    the previously hashed password to compare against.
     * @return true if the plaintext password matches the hashed password,
     *         false otherwise.
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return bcrypt.matches(plainTextPassword, hashedPassword);
    }
}
