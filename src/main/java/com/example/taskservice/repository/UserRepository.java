package com.example.taskservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskservice.model.User;
import com.example.taskservice.model.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findOneByUserRole(UserRole userRole);
}