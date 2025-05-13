package com.example.taskservice.controller;

import com.example.taskservice.AbstractContainerBaseTest;
import com.example.taskservice.security.JwtUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskControllerIT extends AbstractContainerBaseTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil; // Use the real JwtUtil from your application

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        String token = jwtUtil.generateToken("testuser"); // Generate token with JwtUtil
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void testGetAllTasks() {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks", HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetAllTasksWithStatus() {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks?status=TO_DO", HttpMethod.GET, entity,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateTask() {
        String taskJson = "{\"title\":\"Test Task\",\"description\":\"Test Desc\",\"priority\":\"MED\",\"status\":\"TO_DO\"}";
        HttpEntity<String> entity = new HttpEntity<>(taskJson, headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks", HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateTask() throws Exception {
        // Step 1: Create a task to ensure it exists
        String taskJson = "{\"title\":\"Test Task\",\"description\":\"Test Desc\",\"priority\":\"MED\",\"status\":\"TO_DO\"}";
        HttpEntity<String> createEntity = new HttpEntity<>(taskJson, headers);
        ResponseEntity<String> createResponse = restTemplate.exchange("/api/tasks", HttpMethod.POST, createEntity,
                String.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // Step 2: Extract the task ID from the create response
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseBody = objectMapper.readValue(
                createResponse.getBody(),
                new TypeReference<Map<String, Object>>() {
                });
        Long taskId = ((Number) responseBody.get("id")).longValue();

        // Step 3: Update the task using the extracted ID
        String updateJson = "{\"title\":\"Test Task Updated\",\"description\":\"Test Desc\",\"priority\":\"MED\",\"status\":\"TO_DO\"}";
        HttpEntity<String> updateEntity = new HttpEntity<>(updateJson, headers);
        ResponseEntity<String> updateResponse = restTemplate.exchange("/api/tasks/" + taskId, HttpMethod.PUT,
                updateEntity, String.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    }

    @Test
    void testGetTaskNotFound() {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks/999", HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}