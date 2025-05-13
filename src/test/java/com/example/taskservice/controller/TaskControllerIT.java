package com.example.taskservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.taskservice.AbstractContainerBaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("local")
public class TaskControllerIT extends AbstractContainerBaseTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetAllTasks() {
        ResponseEntity<String> response = restTemplate.getForEntity("/tasks", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateTask() {
        String taskJson = "{\"title\":\"Test Task\",\"description\":\"Test Desc\",\"priority\":\"MED\",\"status\":\"TO_DO\"}";
        ResponseEntity<String> response = restTemplate.postForEntity("/tasks", taskJson, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testGetTaskNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("/tasks/999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}