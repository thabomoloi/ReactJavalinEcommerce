package com.oasisnourish.dto;

/**
 * A Data Transfer Object (DTO) representing user input data for creating or
 * updating a user.
 */
public class UserInputDto {
    private int id;
    private String name;
    private String email;
    private String password;

    /**
     * No-argument constructor for creating a {@link UserInputDto} instance.
     * This constructor allows for the creation of an empty DTO, which can
     * be populated using setter methods.
     */
    public UserInputDto() {
    }

    /**
     * Parameterized constructor for creating a {@link UserInputDto} instance
     * with specified values for all fields.
     *
     * @param name     the user's name.
     * @param email    the user's email.
     * @param password the user's password.
     */
    public UserInputDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Gets the user's ID.
     *
     * @return the user's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user's ID.
     *
     * @param id the user's ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user's name.
     *
     * @return the user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name the user's name to set.
     */
    public void setName(String name) {
        this.name = name;
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
