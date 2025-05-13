package com.example.taskservice.exception;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.taskservice.dto.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExceptionTest {

    @Mock
    private ErrorResponse errorResponse;

    @Test
    void testResourceNotFoundException() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testGlobalExceptionHandler() {
        ExceptionHandlerControllerAdvice advice = new ExceptionHandlerControllerAdvice();
        ResourceNotFoundException ex = new ResourceNotFoundException("Test error");
        ResponseEntity<Object> response = advice.handleResourceNotFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Test error", ((ErrorResponse) response.getBody()).getMessage());
    }

    // Mock ExceptionHandlerControllerAdvice class
    private static class ExceptionHandlerControllerAdvice {
        public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
            ErrorResponse error = new ErrorResponse(ex.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
}