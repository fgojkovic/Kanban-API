package com.example.taskservice.mapper;

import com.example.taskservice.dto.TaskRequest;
import com.example.taskservice.dto.TaskResponse;
import com.example.taskservice.model.Priority;
import com.example.taskservice.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskRequest toRequest(Task task) {
        TaskRequest request = new TaskRequest();
        request.setTitle(task.getTitle());
        request.setDescription(task.getDescription());
        request.setStatus(task.getStatus());
        request.setPriority(task.getPriority());
        request.setUserId(task.getUserId());

        return request;
    }

    public Task toEntity(TaskRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());
        task.setUserId(taskRequest.getUserId());

        return task;
    }

    public TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setUserId(task.getUserId());

        return response;
    }

    public void updateEntity(Task task, TaskRequest request) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MED); // Default for updates
        task.setUserId(request.getUserId());
    }
}