package com.example.taskservice.service;

import com.example.taskservice.dto.TaskRequest;
import com.example.taskservice.dto.TaskResponse;
import com.example.taskservice.mapper.TaskMapper;
import com.example.taskservice.model.Task;
import com.example.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");

        taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setTitle("Test Task");
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
        List<TaskResponse> responses = Collections.singletonList(taskResponse);

        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        List<TaskResponse> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
        verify(taskRepository).findAll();
        verify(taskMapper).toResponse(task);
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTask(1L, taskRequest);

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