package com.example.taskservice.mapper;

import com.example.taskservice.dto.LoginRequest;
import com.example.taskservice.dto.UserResponse;
import com.example.taskservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void shouldMapLoginRequestToEntity() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("pass1234");

        User user = userMapper.toEntity(request);

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("", user.getPassword());
    }

    @Test
    void shouldMapUserToResponse() {
        User user = new User("testuser", "hashedpass");

        UserResponse response = userMapper.toResponse(user);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        UserMapper userMapper = new UserMapper();
        assertThrows(IllegalArgumentException.class, () -> userMapper.toResponse(null));
    }
}