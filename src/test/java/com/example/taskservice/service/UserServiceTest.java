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

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        user = new User("user", "pass1234");
        userResponse = new UserResponse("user");
    }

    @Test
    void shouldLoginSuccessfully() {
        when(passwordEncoder.encode("pass1234")).thenReturn("hashedPassword");
        when(passwordEncoder.matches("pass1234", "hashedPassword")).thenReturn(true);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        userService.addUser("user", "pass1234");

        UserResponse result = userService.login("user", "pass1234");

        assertEquals("user", result.getUsername());
    }

    @Test
    void shouldFailLoginWithWrongPassword() {
        when(passwordEncoder.matches("wrongPass", "hashedPassword")).thenReturn(false);
        userService.addUser("user", "pass1234");

        assertThrows(RuntimeException.class, () -> userService.login("user", "wrongPass"));
    }

    @Test
    void shouldFailLoginWithNullPassword() {
        userService.addUser("user", "pass1234");

        assertThrows(RuntimeException.class, () -> userService.login("user", null));
    }

    @Test
    void shouldAddUserSuccessfully() {
        when(passwordEncoder.encode("pass1234")).thenReturn("hashedPassword");

        userService.addUser("user", "pass1234");
        System.out.println("Users map size: " + userService.getAllUsers().size()); // Add getter in UserService

        User result = userService.findUserByUsername("user");
        assertNotNull(result, "User should be added to the map");
        assertEquals("user", result.getUsername());
        assertEquals("hashedPassword", result.getPassword());
    }

    @Test
    void shouldFailAddUserWithDuplicateUsername() {
        when(passwordEncoder.encode("pass1234")).thenReturn("hashedPassword");
        when(passwordEncoder.encode("pass5678")).thenReturn("differentHash"); // Mock for second call
        when(passwordEncoder.matches("pass1234", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.matches("pass1234", "differentHash")).thenReturn(false);

        userService.addUser("user", "pass1234");
        userService.addUser("user", "pass5678");

        User user = userService.findUserByUsername("user");
        assertEquals("user", user.getUsername());
        assertEquals("hashedPassword", user.getPassword()); // Verify the original password
        assertTrue(passwordEncoder.matches("pass1234", user.getPassword()));
    }
}