package com.example.taskservice.service;

import com.example.taskservice.dto.UserResponse;
import com.example.taskservice.mapper.UserMapper;
import com.example.taskservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        when(passwordEncoder.encode("pass1234")).thenReturn("hashedpass");
        when(passwordEncoder.matches("pass1234", "hashedpass")).thenReturn(true);
        when(passwordEncoder.matches("wrongpass", "hashedpass")).thenReturn(false);

        // Pre-populate the users map using the UserService's addUser method
        userService.addUser("user", "pass1234");

        // Mock userMapper.toResponse
        UserResponse response = new UserResponse();
        response.setUsername("user");
        when(userMapper.toResponse(any(User.class))).thenReturn(response);
    }

    @Test
    void shouldLoginSuccessfully() {
        UserResponse result = userService.login("user", "pass1234");

        assertEquals("user", result.getUsername());
    }

    @Test
    void shouldFailLoginWithWrongPassword() {
        UserResponse result = userService.login("user", "wrongpass");

        assertNull(result);
    }
}