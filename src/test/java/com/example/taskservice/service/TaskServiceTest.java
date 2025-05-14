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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequest taskRequest;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setStatus(Status.TO_DO);
        task.setPriority(Priority.MED);

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setStatus(Status.TO_DO);
        taskRequest.setPriority(Priority.MED);

        taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setTitle("Test Task");
        taskResponse.setStatus(Status.TO_DO);
        taskResponse.setPriority(Priority.MED);
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        when(taskMapper.toEntity(taskRequest)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.createTask(taskRequest);

        assertEquals("Test Task", result.getTitle());
        assertEquals(1L, result.getId());
        verify(taskMapper).toEntity(taskRequest);
        verify(taskRepository).save(task);
        verify(taskMapper).toResponse(task);
    }

    @Test
    void shouldGetTaskSuccessfully() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.getTask(1L);

        assertEquals("Test Task", result.getTitle());
        assertEquals(1L, result.getId());
        verify(taskRepository).findById(1L);
        verify(taskMapper).toResponse(task);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.getTask(1L));
        verify(taskRepository).findById(1L);
    }

    @Test
    void shouldGetAllTasksSuccessfully() {
        List<Task> tasks = Collections.singletonList(task);
        Page<Task> taskPage = new PageImpl<>(tasks, Pageable.ofSize(1), tasks.size());

        when(taskRepository.findAll(any(Pageable.class))).thenReturn(taskPage);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        Page<TaskResponse> result = taskService.getAllTasks(Pageable.ofSize(1));

        assertEquals(1, result.getContent().size());
        assertEquals("Test Task", result.getContent().get(0).getTitle());
        verify(taskRepository).findAll(any(Pageable.class));
        verify(taskMapper).toResponse(task);
    }

    @Test
    void shouldGetAllTasksUsingStatusSuccessfully() {
        List<Task> tasks = Collections.singletonList(task);
        Page<Task> taskPage = new PageImpl<>(tasks, Pageable.ofSize(1), tasks.size());

        when(taskRepository.findByStatus(Status.TO_DO, Pageable.ofSize(1))).thenReturn(taskPage);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        Page<TaskResponse> result = taskService.getTasksByStatus(Status.TO_DO, Pageable.ofSize(1));

        assertEquals(1, result.getContent().size());
        assertEquals("Test Task", result.getContent().get(0).getTitle());
        verify(taskRepository).findByStatus(Status.TO_DO, Pageable.ofSize(1));
        verify(taskMapper).toResponse(task);
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTask(1L, taskRequest);
        System.out.println("Task before update: " + task);

        assertEquals("Test Task", result.getTitle());
        verify(taskRepository).findById(1L);
        verify(taskMapper).updateEntity(task, taskRequest);
        verify(taskRepository).save(task);
        verify(taskMapper).toResponse(task);
    }

    @Test
    void shouldDeleteTaskSuccessfully() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).existsById(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTask() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> taskService.deleteTask(1L));
        verify(taskRepository).existsById(1L);
        verify(taskRepository, never()).deleteById(1L);
    }
}