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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceIT extends AbstractContainerBaseTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    // @AfterEach
    // void tearDown() {
    // taskRepository.deleteAll();
    // }

    @Test
    @Transactional
    @Rollback
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

    @Test
    @Transactional
    @Rollback
    void shouldUpdateTask() {
        TaskRequest createRequest = new TaskRequest();
        createRequest.setTitle("Original Task");
        createRequest.setStatus(Status.TO_DO);
        createRequest.setPriority(Priority.MED);
        TaskResponse createdTask = taskService.createTask(createRequest);

        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Desc");
        updateRequest.setStatus(Status.IN_PROGRESS);
        updateRequest.setPriority(Priority.HIGH);

        TaskResponse updatedTask = taskService.updateTask(createdTask.getId(), updateRequest);

        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated Desc", updatedTask.getDescription());
        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus());
        assertEquals(Priority.HIGH, updatedTask.getPriority());
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Task to Delete");
        request.setStatus(Status.TO_DO);
        request.setPriority(Priority.MED);
        TaskResponse createdTask = taskService.createTask(request);

        taskService.deleteTask(createdTask.getId());

        assertFalse(taskRepository.findById(createdTask.getId()).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionForNonExistentTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Dummy Task");
        request.setStatus(Status.TO_DO);
        request.setPriority(Priority.MED);

        assertThrows(RuntimeException.class,
                () -> taskService.updateTask(999L, request));
    }

    @Test
    @Transactional
    @Rollback
    void shouldFilterTasksByStatus() {
        TaskRequest task1 = new TaskRequest();
        task1.setTitle("Task 1");
        task1.setStatus(Status.TO_DO);
        task1.setPriority(Priority.MED);
        taskService.createTask(task1);

        TaskRequest task2 = new TaskRequest();
        task2.setTitle("Task 2");
        task2.setStatus(Status.IN_PROGRESS);
        task2.setPriority(Priority.HIGH);
        taskService.createTask(task2);

        Page<TaskResponse> todoTasks = taskService.getTasksByStatus(Status.TO_DO, Pageable.ofSize(1));
        assertEquals(1, todoTasks.getSize());
        assertEquals("Task 1", todoTasks.getContent().get(0).getTitle());
        assertEquals(Status.TO_DO, todoTasks.getContent().get(0).getStatus());
    }
}