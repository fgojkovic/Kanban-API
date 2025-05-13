package com.example.taskservice.exception;

import com.example.taskservice.dto.ErrorResponse;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExceptionTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/test-uri");
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Resource not found");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRuntimeException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Response body should not be null");
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getError());
        assertEquals("Resource not found", errorResponse.getMessage());
        assertEquals("/test-uri", errorResponse.getPath());
        assertEquals(List.of("Resource not found"), errorResponse.getDetails());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Response body should not be null");
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Bad Request", errorResponse.getError());
        assertEquals("Invalid argument", errorResponse.getMessage());
        assertEquals("/test-uri", errorResponse.getPath());
        assertEquals(List.of("Invalid argument"), errorResponse.getDetails());
    }

    @Test
    void testHandleOptimisticLockException() {
        OptimisticLockException ex = new OptimisticLockException("Version mismatch");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOptimisticLock(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Response body should not be null");
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getStatus());
        assertEquals("Conflict", errorResponse.getError());
        assertEquals("Conflict due to version mismatch", errorResponse.getMessage());
        assertEquals("/test-uri", errorResponse.getPath());
        assertEquals(List.of("Version mismatch"), errorResponse.getDetails());
    }

    @Test
    void testHandleGeneralException() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneralException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Response body should not be null");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("An unexpected error occurred", errorResponse.getMessage());
        assertEquals("/test-uri", errorResponse.getPath());
        assertEquals(List.of("Unexpected error"), errorResponse.getDetails());
    }
}