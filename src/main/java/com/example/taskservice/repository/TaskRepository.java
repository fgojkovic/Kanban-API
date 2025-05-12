package com.example.taskservice.repository;

import com.example.taskservice.model.Task;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.taskservice.model.Status;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByStatus(Status status, Pageable pageable);
    // You can add custom query methods here if needed
    // For example, findByStatus, findByUserId, etc.
    // Example: List<Task> findByStatus(String status);
    // Example: List<Task> findByUserId(Long userId);
    // Example: List<Task> findByPriority(String priority);
    // Example: List<Task> findByTitleContaining(String title);
    // Example: List<Task> findByDescriptionContaining(String description);
    // Example: List<Task> findByCreatedAtBetween(Date startDate, Date endDate);
    // Example: List<Task> findByUpdatedAtBetween(Date startDate, Date endDate);
    // Example: List<Task> findByCreatedAtAfter(Date date);
    // Example: List<Task> findByUpdatedAtAfter(Date date);
    // Example: List<Task> findByCreatedAtBefore(Date date);
    // Example: List<Task> findByUpdatedAtBefore(Date date);
    // Example: List<Task> findByCreatedAt(Date date);
    // Example: List<Task> findByUpdatedAt(Date date);
    // Example: List<Task> findByCreatedAtIn(List<Date> dates);
    // Example: List<Task> findByUpdatedAtIn(List<Date> dates);
}
