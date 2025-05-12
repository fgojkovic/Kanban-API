package com.example.taskservice.mapper;

import com.example.taskservice.dto.TaskRequest;
import com.example.taskservice.dto.TaskResponse;
import com.example.taskservice.model.Priority;
import com.example.taskservice.model.Status;
import com.example.taskservice.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TaskMapperTest {

    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapper();
    }

    @Test
    void shouldMapTaskRequestToEntity() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setStatus(Status.TO_DO);
        request.setPriority(Priority.HIGH);
        request.setUserId(1L);

        Task task = taskMapper.toEntity(request);

        assertNotNull(task);
        assertEquals("Test Task", task.getTitle());
        assertEquals("Test Description", task.getDescription());
        assertEquals(Status.TO_DO, task.getStatus());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(1L, task.getUserId());
    }

    @Test
    void shouldMapTaskToResponse() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(Status.TO_DO);
        task.setPriority(Priority.HIGH);
        task.setUserId(1L);

        TaskResponse response = taskMapper.toResponse(task);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals(Status.TO_DO, response.getStatus());
        assertEquals(Priority.HIGH, response.getPriority());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void shouldHandleNullFieldsInRequest() {
        TaskRequest request = new TaskRequest();
        request.setTitle(null);
        request.setDescription(null);

        Task task = taskMapper.toEntity(request);

        assertEquals(null, task.getTitle()); // Assuming default value
        assertNull(task.getDescription());
    }

    @Test
    void shouldUpdateTaskFromRequest() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Old Title");
        task.setDescription("Old Description");
        task.setStatus(Status.TO_DO);
        task.setPriority(Priority.MED);

        TaskRequest request = new TaskRequest();
        request.setTitle("New Title");
        request.setDescription("New Description");
        request.setStatus(Status.TO_DO);
        request.setPriority(Priority.MED);

        taskMapper.updateEntity(task, request);

        assertEquals("New Title", task.getTitle());
        assertEquals("New Description", task.getDescription());
        assertEquals("TODO", task.getStatus());
        assertEquals("HIGH", task.getPriority());
        assertEquals(1L, task.getId()); // Ensure ID is unchanged
    }
}