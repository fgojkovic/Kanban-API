package com.example.taskservice.service;

import com.example.taskservice.dto.TaskRequest;
import com.example.taskservice.dto.TaskResponse;
import com.example.taskservice.mapper.TaskMapper;
import com.example.taskservice.model.Priority;
import com.example.taskservice.model.Status;
import com.example.taskservice.model.Task;
import com.example.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setStatus(Status.TO_DO);
        request.setPriority(Priority.HIGH);
        request.setUserId(1L);

        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(Status.TO_DO);
        task.setPriority(Priority.HIGH);
        task.setUserId(1L);
        task.setId(1L);

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle("Test Task");
        response.setDescription("Test Description");
        response.setStatus(Status.TO_DO);
        response.setPriority(Priority.HIGH);
        response.setUserId(1L);

        when(taskMapper.toEntity(request)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.createTask(request);

        assertEquals(1L, result.getId());
        assertEquals("Test Task", result.getTitle());
        verify(messagingTemplate).convertAndSend("/topic/tasks", task);
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        Long id = 1L;
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated Task");
        request.setStatus(Status.IN_PROGRESS);

        Task existingTask = new Task();
        existingTask.setId(id);
        existingTask.setTitle("Old Task");
        existingTask.setStatus(Status.TO_DO);

        Task updatedTask = new Task();
        updatedTask.setId(id);
        updatedTask.setTitle("Updated Task");
        updatedTask.setStatus(Status.IN_PROGRESS);

        TaskResponse response = new TaskResponse();
        response.setId(id);
        response.setTitle("Updated Task");
        response.setStatus(Status.IN_PROGRESS);

        when(taskRepository.findById(id)).thenReturn(java.util.Optional.of(existingTask));
        when(taskMapper.toEntity(request)).thenReturn(updatedTask);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toResponse(updatedTask)).thenReturn(response);

        TaskResponse result = taskService.updateTask(id, request);

        assertEquals("Updated Task", result.getTitle());
        assertEquals(Status.IN_PROGRESS, result.getStatus());
        verify(messagingTemplate).convertAndSend("/topic/tasks", updatedTask);
    }
}