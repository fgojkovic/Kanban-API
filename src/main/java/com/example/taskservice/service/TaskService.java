package com.example.taskservice.service;

import com.example.taskservice.dto.TaskRequest;
import com.example.taskservice.dto.TaskResponse;
import com.example.taskservice.mapper.TaskMapper;
import com.example.taskservice.model.Status;
import com.example.taskservice.model.Task;
import com.example.taskservice.repository.TaskRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final TaskMapper taskMapper;
    private static final String TASKS_TOPIC = "/topic/tasks";

    // Constructor injection for TaskRepository
    public TaskService(TaskRepository taskRepository, SimpMessagingTemplate messagingTemplate, TaskMapper taskMapper) {
        this.messagingTemplate = messagingTemplate;
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    // Method to get all tasks
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskMapper::toResponse);
    }

    public Page<TaskResponse> getTasksByStatus(Status status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable)
                .map(taskMapper::toResponse);
    }

    public TaskResponse getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return taskMapper.toResponse(task);
    }

    // Method to create a new task
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task task = taskMapper.toEntity(taskRequest);
        Task savedTask = taskRepository.save(task);

        // Emit WebSocket event
        messagingTemplate.convertAndSend(TASKS_TOPIC, savedTask);

        return taskMapper.toResponse(savedTask);
    }

    // Method to update an existing task
    public TaskResponse updateTask(Long id, TaskRequest taskRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        taskMapper.updateEntity(task, taskRequest);
        task = taskRepository.save(task);
        TaskResponse response = taskMapper.toResponse(task);
        messagingTemplate.convertAndSend(TASKS_TOPIC, response);

        return response;
    }

    // Method to delete a task by ID
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }

        taskRepository.deleteById(id);

        // Emit WebSocket event with null or a deletion marker (e.g., task ID)
        messagingTemplate.convertAndSend(TASKS_TOPIC, id);
    }
}
