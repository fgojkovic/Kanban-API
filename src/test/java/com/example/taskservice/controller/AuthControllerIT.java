package com.example.taskservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
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

import com.example.taskservice.AbstractContainerBaseTest;
import com.example.taskservice.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerIT extends AbstractContainerBaseTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @Rollback
    void testLoginFailsIfUserNotFound() {
        String taskJson = "{\"username\":\"userMissing\",\"password\":\"pass1234\"}";
        HttpEntity<String> entity = new HttpEntity<>(taskJson, headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/auth/login", HttpMethod.POST, entity,
                String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testLoginFailsWithMissingUsername() {
        String taskJson = "{\"username\":null,\"password\":\"pass1234\"}";
        HttpEntity<String> entity = new HttpEntity<>(taskJson, headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/auth/login", HttpMethod.POST, entity,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testRegisterAdminUser() {
        String taskJson = "{\"username\":\"user\",\"password\":\"pass1234\",\"role\":\"ADMIN\"}";
        HttpEntity<String> entity = new HttpEntity<>(taskJson, headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/auth/register", HttpMethod.POST, entity,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testRegisterUserFailsWithWrongEnumType() {
        String taskJson = "{\"username\":\"user\",\"password\":\"pass1234\",\"role\":\"SUPER\"}";
        HttpEntity<String> entity = new HttpEntity<>(taskJson, headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/auth/register", HttpMethod.POST, entity,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testLoginWithAdmin() {
        String taskJson = "{\"username\":\"user\",\"password\":\"pass1234\"}";
        HttpEntity<String> entity = new HttpEntity<>(taskJson, headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/auth/login", HttpMethod.POST, entity,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
