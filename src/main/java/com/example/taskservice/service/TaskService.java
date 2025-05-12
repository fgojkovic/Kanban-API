package com.example.taskservice.service;

import com.example.taskservice.dto.TaskRequest;
import com.example.taskservice.dto.TaskResponse;
import com.example.taskservice.mapper.TaskMapper;
import com.example.taskservice.model.Status;
import com.example.taskservice.model.Task;
import com.example.taskservice.repository.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<Task> getTasksByStatus(Status status, Pageable pageable) {
        if (status != null) {
            return taskRepository.findByStatus(status, pageable);
        }
        return taskRepository.findAll(pageable);
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
        Task task = taskMapper.toEntity(taskRequest);
        task.setId(id);

        Task updatedTask = taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(task.getTitle());
                    existingTask.setDescription(task.getDescription());
                    existingTask.setStatus(task.getStatus());
                    existingTask.setPriority(task.getPriority());
                    existingTask.setUserId(task.getUserId());
                    return taskRepository.save(existingTask);
                })
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id));

        // Emit WebSocket event
        messagingTemplate.convertAndSend(TASKS_TOPIC, updatedTask);

        return taskMapper.toResponse(updatedTask);
    }

    // Method to partially update a task
    public TaskResponse partialUpdateTask(Long id, TaskRequest taskRequest) {
        Task partialTask = taskMapper.toEntity(taskRequest);
        partialTask.setId(id);

        Task partialyUpdatedTask = taskRepository.findById(id)
                .map(existingTask -> {
                    if (partialTask.getTitle() != null) {
                        existingTask.setTitle(partialTask.getTitle());
                    }
                    if (partialTask.getDescription() != null) {
                        existingTask.setDescription(partialTask.getDescription());
                    }
                    if (partialTask.getStatus() != null) {
                        existingTask.setStatus(partialTask.getStatus());
                    }
                    if (partialTask.getPriority() != null) {
                        existingTask.setPriority(partialTask.getPriority());
                    }
                    if (partialTask.getUserId() != null) {
                        existingTask.setUserId(partialTask.getUserId());
                    }
                    return taskRepository.save(existingTask);
                })
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id));

        // Emit WebSocket event
        messagingTemplate.convertAndSend(TASKS_TOPIC, partialyUpdatedTask);

        return taskMapper.toResponse(partialyUpdatedTask);
    }

    // Method to delete a task by ID
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);

        // Emit WebSocket event with null or a deletion marker (e.g., task ID)
        messagingTemplate.convertAndSend(TASKS_TOPIC, id);
    }
}
