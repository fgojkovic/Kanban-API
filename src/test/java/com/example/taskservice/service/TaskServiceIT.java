package com.example.taskservice.service;

import com.example.taskservice.AbstractContainerBaseTest;
import com.example.taskservice.dto.TaskRequest;
import com.example.taskservice.dto.TaskResponse;
import com.example.taskservice.model.Priority;
import com.example.taskservice.model.Status;
import com.example.taskservice.model.Task;
import com.example.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("local")
class TaskServiceIT extends AbstractContainerBaseTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Integration Task");
        request.setStatus(Status.TO_DO);
        request.setPriority(Priority.MED);

        TaskResponse createdTask = taskService.createTask(request);

        assertNotNull(createdTask.getId());
        assertEquals("Integration Task", createdTask.getTitle());
        assertEquals(Status.TO_DO, createdTask.getStatus());
        assertEquals(Priority.MED, createdTask.getPriority());

        List<Task> savedTasks = taskRepository.findAll();
        assertEquals(1, savedTasks.size());
        assertEquals("Integration Task", savedTasks.get(0).getTitle());
    }
}