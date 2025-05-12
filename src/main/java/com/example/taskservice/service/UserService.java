package com.example.taskservice.service;

import com.example.taskservice.dto.UserResponse;
import com.example.taskservice.mapper.UserMapper;
import com.example.taskservice.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final Map<String, User> users = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass1234";

    public UserService(BCryptPasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        // Pre-populate a user for testing (equivalent to "user:pass")
        // ovo je nekak skužilo da size pass nije bilo veće od 6 znamenki dok sam
        // koristil samo pass
        // String hashedPassword = passwordEncoder.encode("pass");
        String hashedPassword = passwordEncoder.encode(PASSWORD);
        users.put(USERNAME, new User(USERNAME, hashedPassword));
    }

    public UserResponse login(String username, String password) {
        User user = users.get(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return userMapper.toResponse(user);
        }
        return null; // Handle authentication failure elsewhere
    }

    public User findUserByUsername(String username) {
        return users.get(username);
    }

    // Method to add a new user (e.g., for registration)
    public void addUser(String username, String rawPassword) {
        if (users.containsKey(username)) {
            return;
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        users.put(username, new User(username, hashedPassword));
    }

    public boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}