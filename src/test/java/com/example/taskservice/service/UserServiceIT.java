package com.example.taskservice.service;

import com.example.taskservice.AbstractContainerBaseTest;
import com.example.taskservice.dto.UserResponse;
import com.example.taskservice.model.User;
import com.example.taskservice.model.UserRole;
import com.example.taskservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIT extends AbstractContainerBaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @Rollback
    void shouldAddAndLoginUserSuccessfully() {
        // Add a user
        userService.addUser("testuser", "password123", UserRole.USER);

        // Verify user is in the database
        User savedUser = userRepository.findByUsername("testuser").orElse(null);
        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());

        // Login with correct credentials
        UserResponse response = userService.login("testuser", "password123");
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());

        // Login with incorrect password
        assertThrows(RuntimeException.class, () -> userService.login("testuser", "wrongpassword"));
    }
}
