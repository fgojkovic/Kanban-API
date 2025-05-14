package com.example.taskservice.service;

import com.example.taskservice.dto.UserResponse;
import com.example.taskservice.mapper.UserMapper;
import com.example.taskservice.model.User;
import com.example.taskservice.model.UserRole;
import com.example.taskservice.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserService(BCryptPasswordEncoder passwordEncoder, UserMapper userMapper, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    public UserResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return userMapper.toResponse(user);
        }
        throw new RuntimeException("Invalid password");
    }

    public UserResponse register(String username, String password, UserRole userRole) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        if (userRepository.findOneByUserRole(UserRole.ADMIN).isPresent() && userRole == UserRole.ADMIN) {
            throw new RuntimeException("Admin already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserRole(userRole);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public void addUser(String username, String rawPassword, UserRole userRole) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setUserRole(userRole);
        userRepository.save(user);
    }

    public boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}