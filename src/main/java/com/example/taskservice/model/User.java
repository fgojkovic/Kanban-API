package com.example.taskservice.model;

import com.example.taskservice.config.UserRoleLenientDeserializer;
import com.example.taskservice.validation.EnumValidator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonDeserialize(using = UserRoleLenientDeserializer.class)
    @EnumValidator(enumClass = UserRole.class, message = "Status must be one of: ADMIN, USER, GUEST")
    UserRole userRole;;

    @Version
    private Long version;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}