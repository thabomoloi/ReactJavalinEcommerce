package com.oasisnourish.dto;

/**
 * A Data Transfer Object (DTO) representing user input data for creating or
 * updating a user.
 */
public class UserInputDto extends UserAuthDto {
    private int id;
    private String name;

    /**
     * No-argument constructor for creating a {@link UserInputDto} instance.
     * This constructor allows for the creation of an empty DTO, which can
     * be populated using setter methods.
     */
    public UserInputDto() {
        super();
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
        super(email, password);
        this.name = name;
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
}
