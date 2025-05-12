package com.example.taskservice.mapper;

import com.example.taskservice.dto.LoginRequest;
import com.example.taskservice.dto.UserResponse;
import com.example.taskservice.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(LoginRequest loginRequest) {
        User user = new User(loginRequest.getUsername(), ""); // Password handled by service
        return user;
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return new UserResponse(user.getUsername());
    }
}