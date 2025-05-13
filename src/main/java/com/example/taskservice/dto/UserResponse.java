package com.example.taskservice.dto;

import com.example.taskservice.model.UserRole;

public class UserResponse {
    private String username;
    private UserRole userRole;

    public UserResponse() {
    }

    public UserResponse(String username, UserRole userRole) {
        this.username = username;
        this.userRole = userRole;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}