package com.oasisnourish.models;

import java.time.LocalDateTime;

import com.oasisnourish.enums.Role;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private LocalDateTime emailVerified;

    public User() {
        this("", "", "");
    }

    public User(String name, String email, String password) {
        this(name, email, password, Role.UNVERIFIED_USER);
    }

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(LocalDateTime emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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
