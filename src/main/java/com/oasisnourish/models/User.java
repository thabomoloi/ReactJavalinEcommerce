package com.oasisnourish.models;

import java.time.LocalDateTime;

import com.oasisnourish.enums.Role;

/**
 * Represents a user in the system with attributes such as ID, name, email,
 * password, role, and email verification status.
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private LocalDateTime emailVerified;

    /**
     * Default constructor initializing a user with empty name, email, and password.
     */
    public User() {
        this("", "", "");
    }

    /**
     * Constructs a User with the specified name, email, and password.
     *
     * @param name     the name of the user
     * @param email    the email of the user
     * @param password the password of the user
     */
    public User(String name, String email, String password) {
        this(name, email, password, Role.UNVERIFIED_USER);
    }

    /**
     * Constructs a User with the specified name, email, password, and role.
     *
     * @param name     the name of the user
     * @param email    the email of the user
     * @param password the password of the user
     * @param role     the role of the user
     */
    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
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
     * Gets the user's password.
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password the user's password to set
     */
    public void setPassword(String password) {
        this.password = password;
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
    public LocalDateTime getEmailVerified() {
        return emailVerified;
    }

    /**
     * Sets the user's email verification timestamp.
     *
     * @param emailVerified the timestamp to set when the user's email was verified
     */
    public void setEmailVerified(LocalDateTime emailVerified) {
        this.emailVerified = emailVerified;
    }

    /**
     * Returns a string representation of the User object.
     *
     * @return a string representation of the user, including ID, name, email, role,
     *         and email verification status
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", emailVerified=" + (emailVerified != null ? emailVerified.toString() : "not verified") +
                '}';
    }
}
