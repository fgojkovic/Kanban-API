package com.example.taskservice.service;

import com.example.taskservice.dto.UserResponse;
import com.example.taskservice.mapper.UserMapper;
import com.example.taskservice.model.User;
import com.example.taskservice.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

public class UserServiceTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        user = new User("user", "pass1234");
        userResponse = new UserResponse(user.getUsername());
    }

    @Test
    void shouldLoginSuccessfully() {
        when(passwordEncoder.encode("pass1234")).thenReturn("hashedPassword");
        when(passwordEncoder.matches("pass1234", "hashedPassword")).thenReturn(true);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        UserResponse result = userService.login("user", "pass1234");

        assertEquals("user", result.getUsername());
    }

    @Test
    void shouldFailLoginWithWrongPassword() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "hashedPassword")).thenReturn(false);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        assertThrows(RuntimeException.class, () -> userService.login("user", "wrongPass"));
    }

    @Test
    void shouldFailLoginWithNullPassword() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        assertThrows(RuntimeException.class, () -> userService.login("user", null));
    }

    @Test
    void shouldAddUserSuccessfully() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass1234")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L); // Simulate database ID generation
            return savedUser;
        });

        userService.addUser("user", "pass1234");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldFailAddUserWithDuplicateUsername() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("pass1234")).thenReturn("hashedPassword");

        userService.addUser("user", "pass1234");

        verify(userRepository, never()).save(any(User.class));
    }
}