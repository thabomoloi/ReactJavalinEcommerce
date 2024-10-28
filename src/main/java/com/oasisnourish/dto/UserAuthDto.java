package com.oasisnourish.dto;

/**
 * A Data Transfer Object (DTO) representing login details.
 */
public class UserAuthDto {
    private String email;
    private String password;

    /**
     * No-argument constructor for creating a {@link UseAuthDto} instance.
     * This constructor allows for the creation of an empty DTO, which can
     * be populated using setter methods.
     */
    public UserAuthDto() {
    }

    /**
     * Parameterized constructor for creating a {@link UserInputDto} instance
     * with specified values for all fields.
     *
     * @param email    the user's email.
     * @param password the user's password.
     */
    public UserAuthDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Gets the user's email.
     *
     * @return the user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email the user's email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's password.
     *
     * @return the user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password the user's password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
