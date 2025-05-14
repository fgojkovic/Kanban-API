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
    private JwtUtil jwtUtil;

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        String token = jwtUtil.generateToken("testuser");
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
        ResponseEntity<String> response = this.createTask();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateTask() throws Exception {
        ResponseEntity<String> createResponse = this.createTask();
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        Long taskId = this.extractIdFromResponse(createResponse);

        String updateJson = "{\"title\":\"Test Task Updated\",\"description\":\"Test Desc\",\"priority\":\"MED\",\"status\":\"TO_DO\"}";
        HttpEntity<String> updateEntity = new HttpEntity<>(updateJson, headers);
        ResponseEntity<String> updateResponse = restTemplate.exchange("/api/tasks/" + taskId, HttpMethod.PUT,
                updateEntity, String.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testPartialUpdateTask() throws Exception {
        ResponseEntity<String> createResponse = this.createTask();
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        Long taskId = this.extractIdFromResponse(createResponse);

        String updateJson = "{\"description\":\"Partial updated\",\"priority\":\"HIGH\"}";
        headers.set("Content-Type", "application/merge-patch+json");
        HttpEntity<String> updateEntity = new HttpEntity<>(updateJson, headers);
        ResponseEntity<String> updateResponse = restTemplate.exchange("/api/tasks/" + taskId, HttpMethod.PATCH,
                updateEntity, String.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testPartialUpdateTaskFailsWithWrongEnumTypes() throws Exception {
        ResponseEntity<String> createResponse = this.createTask();
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        Long taskId = this.extractIdFromResponse(createResponse);

        String updateJson = "{\"description\":\"Partial updated\",\"priority\":\"SUPER\",\"status\":\"SUPER\"}";
        headers.set("Content-Type", "application/merge-patch+json");
        HttpEntity<String> updateEntity = new HttpEntity<>(updateJson, headers);
        ResponseEntity<String> updateResponse = restTemplate.exchange("/api/tasks/" + taskId, HttpMethod.PATCH,
                updateEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
    }

    @Test
    void testGetTaskNotFound() {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks/999", HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testGetTask() throws Exception {
        ResponseEntity<String> createResponse = this.createTask();
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        Long taskId = this.extractIdFromResponse(createResponse);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks/" + taskId, HttpMethod.GET, entity,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteTask() throws Exception {
        ResponseEntity<String> createResponse = this.createTask();
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        Long taskId = this.extractIdFromResponse(createResponse);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks/" + taskId, HttpMethod.DELETE, entity,
                String.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteTaskFailsWithTaskNotFound() throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks/999", HttpMethod.DELETE, entity,
                String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private ResponseEntity<String> createTask() {
        String taskJson = "{\"title\":\"Test Task\",\"description\":\"Test Desc\",\"priority\":\"MED\",\"status\":\"TO_DO\"}";
        HttpEntity<String> createEntity = new HttpEntity<>(taskJson, headers);
        return restTemplate.exchange("/api/tasks", HttpMethod.POST, createEntity, String.class);
    }

    private Long extractIdFromResponse(ResponseEntity<String> responseBody) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(
                responseBody.getBody(),
                new TypeReference<Map<String, Object>>() {
                });
        return ((Number) responseMap.get("id")).longValue();
    }
}