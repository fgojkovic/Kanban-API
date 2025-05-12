package com.example.taskservice.controller;

import com.example.taskservice.dto.LoginRequest;
import com.example.taskservice.dto.UserResponse;
import com.example.taskservice.security.JwtUtil;
import com.example.taskservice.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user authentication")
@Validated
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login and obtain JWT token", description = "Authenticate with username and password to receive a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated, token returned"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        if (username == null || password == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username and password are required");
        }

        UserResponse userResponse = userService.login(username, password);
        if (userResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(username);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userResponse.getUsername());

        return ResponseEntity.ok(response);
    }

    // Optional: Add a registration endpoint for testing
    // @PostMapping("/register")
    // @Operation(summary = "Register a new user", description = "Create a new user
    // with a username and password")
    // @ApiResponses(value = {
    // @ApiResponse(responseCode = "200", description = "User registered
    // successfully"),
    // @ApiResponse(responseCode = "400", description = "Bad request - Username
    // already exists or invalid input")
    // })
    // public ResponseEntity<?> register(@RequestBody Map<String, String>
    // credentials) {
    // String username = credentials.get("username");
    // String password = credentials.get("password");

    // if (username == null || password == null) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username and
    // password are required");
    // }

    // if (userService.findUserByUsername(username) != null) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already
    // exists");
    // }

    // userService.addUser(username, password);
    // return ResponseEntity.ok("User registered successfully");
    // }
}