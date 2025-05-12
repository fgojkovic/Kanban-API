package com.example.taskservice.model;

import com.example.taskservice.config.PriorityLenientDeserializer;
import com.example.taskservice.config.StatusLenientDeserializer;
import com.example.taskservice.validation.EnumValidator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonDeserialize(using = StatusLenientDeserializer.class)
    @EnumValidator(enumClass = Status.class, message = "Status must be one of: TO_DO, IN_PROGRESS, DONE")
    private Status status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonDeserialize(using = PriorityLenientDeserializer.class)
    @EnumValidator(enumClass = Priority.class, message = "Priority must be one of: LOW, MED, HIGH")
    private Priority priority;

    private Long userId; // Foreign key to User

    @Version
    private Long version; // Version field for optimistic locking

    // Default constructor for JPA
    public Task() {
    }

    public Task(String title, String description, Status status, Priority priority, Long userId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.userId = userId;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
