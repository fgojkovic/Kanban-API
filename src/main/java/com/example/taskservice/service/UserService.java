package com.example.taskservice.service;

import com.example.taskservice.dto.UserResponse;
import com.example.taskservice.mapper.UserMapper;
import com.example.taskservice.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
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
        // String hashedPassword = passwordEncoder.encode(PASSWORD);
        // users.put(USERNAME, new User(USERNAME, hashedPassword));
    }

    public UserResponse login(String username, String password) {
        User user = findUserByUsername(username);

        if (users.containsKey(username)) {
            user = users.get(username);
        }

        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            return userMapper.toResponse(user);
        }
        throw new RuntimeException("Invalid password");
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

    public List<UserResponse> getAllUsers() {
        return users.values().stream()
                .map(userMapper::toResponse)
                .toList();
    }
}