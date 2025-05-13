package com.example.taskservice.dto;

import com.example.taskservice.config.UserRoleLenientDeserializer;
import com.example.taskservice.model.UserRole;
import com.example.taskservice.validation.EnumValidator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.NotNull;

public class RegisterRequest extends LoginRequest {

    @NotNull(message = "Role is mandatory")
    @JsonDeserialize(using = UserRoleLenientDeserializer.class)
    @EnumValidator(enumClass = UserRole.class, message = "Status must be one of: ADMIN, USER, GUEST")
    private UserRole role;

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
