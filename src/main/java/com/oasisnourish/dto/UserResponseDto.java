package com.oasisnourish.dto;

import java.time.format.DateTimeFormatter;

import com.oasisnourish.enums.Role;
import com.oasisnourish.models.User;

public class UserResponseDto {
    private int id;
    private String name;
    private String email;
    private Role role;
    private String emailVerified;

    /**
     * Constructs a {@link UserResponseDto} with the specified name, email,
     * password, and role.
     *
     * @param name          the name of the user
     * @param email         the email of the user
     * @param role          the role of the user
     * @param emailVerified the date the email was verified
     */
    public UserResponseDto(String name, String email, Role role, String emailVerified) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.emailVerified = emailVerified;
    }

    /**
     * Gets the user's ID.
     *
     * @return the user's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user's ID.
     *
     * @param id the user's ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user's name.
     *
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name the user's name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's email.
     *
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email the user's email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's role.
     *
     * @return the user's role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the user's role.
     *
     * @param role the user's role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Gets the user's email verification timestamp.
     *
     * @return the timestamp when the user's email was verified, or null if not
     *         verified
     */
    public String getEmailVerified() {
        return emailVerified;
    }

    /**
     * Sets the user's email verification timestamp.
     *
     * @param emailVerified the timestamp to set when the user's email was verified
     */
    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        UserResponseDto that = (UserResponseDto) obj;

        return id == that.id &&
                (name != null ? name.equals(that.name) : that.name == null) &&
                (email != null ? email.equals(that.email) : that.email == null) &&
                role == that.role &&
                (emailVerified != null ? emailVerified.equals(that.emailVerified) : that.emailVerified == null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (emailVerified != null ? emailVerified.hashCode() : 0);
        return result;
    }

    /**
     * Converts the {@link User} object to {@link UserResponseDto}
     * 
     * @param user a {@link User} object to convert.
     * @return {@link UserResponseDto} object with user's details.
     */
    public static UserResponseDto fromModel(User user) {
        var dto = new UserResponseDto(
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getEmailVerified() == null ? null
                        : user.getEmailVerified().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setId(user.getId());
        return dto;
    }

}
