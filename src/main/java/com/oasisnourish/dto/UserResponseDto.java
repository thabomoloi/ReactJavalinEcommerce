package com.oasisnourish.dto;

import java.util.Objects;

import com.oasisnourish.enums.Role;
import com.oasisnourish.models.User;

public class UserResponseDto {

    private final int id;
    private final String name;
    private final String email;
    private final Role role;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        UserResponseDto dto = (UserResponseDto) obj;

        return id == dto.id
                && Objects.equals(name, dto.name)
                && Objects.equals(email, dto.email)
                && role == dto.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, role);
    }

}
