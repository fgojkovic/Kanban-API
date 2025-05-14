package com.example.taskservice.dto;

import com.example.taskservice.config.PriorityLenientDeserializer;
import com.example.taskservice.config.StatusLenientDeserializer;
import com.example.taskservice.model.Priority;
import com.example.taskservice.model.Status;
import com.example.taskservice.validation.EnumValidator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TaskResponse {
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Status is mandatory")
    @JsonDeserialize(using = StatusLenientDeserializer.class)
    @EnumValidator(enumClass = Status.class, message = "Status must be one of: TO_DO, IN_PROGRESS, DONE")
    private Status status;

    @NotNull(message = "Priority is mandatory")
    @JsonDeserialize(using = PriorityLenientDeserializer.class)
    @EnumValidator(enumClass = Priority.class, message = "Priority must be one of: LOW, MED, HIGH")
    private Priority priority;
    private Long userId;

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
}