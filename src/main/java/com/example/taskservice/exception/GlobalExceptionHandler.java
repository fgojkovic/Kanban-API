package com.example.taskservice.exception;

import com.example.taskservice.dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

        // Handle validation errors (e.g., @NotBlank, @Size)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {
                List<String> details = new ArrayList<>();
                for (FieldError error : ex.getBindingResult().getFieldErrors()) {
                        details.add(error.getField() + ": " + error.getDefaultMessage());
                }
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                "Validation failed",
                                request.getRequestURI(),
                                details);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle constraint violations (e.g., @Valid on method parameters)
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolation(
                        ConstraintViolationException ex, HttpServletRequest request) {
                List<String> details = new ArrayList<>();
                ex.getConstraintViolations().forEach(
                                violation -> details.add(violation.getPropertyPath() + ": " + violation.getMessage()));
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                "Validation failed",
                                request.getRequestURI(),
                                details);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle illegal argument exceptions (e.g., invalid status in query params)
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(
                        IllegalArgumentException ex, HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of(ex.getMessage()));
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle optimistic lock exceptions (409 Conflict)
        @ExceptionHandler(OptimisticLockException.class)
        public ResponseEntity<ErrorResponse> handleOptimisticLock(
                        OptimisticLockException ex, HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "Conflict",
                                "Conflict due to version mismatch",
                                request.getRequestURI(),
                                List.of(ex.getMessage()));
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        // Handle runtime exceptions (e.g., 404 Not Found)
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErrorResponse> handleRuntimeException(
                        RuntimeException ex, HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Not Found",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of(ex.getMessage()));
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Handle JSON mapping exceptions (e.g., invalid JSON format)
        @ExceptionHandler(JsonMappingException.class)
        public ResponseEntity<ErrorResponse> handleJsonMappingException(
                        JsonMappingException ex, HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of(ex.getMessage()));
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle JSON processing exceptions (e.g., invalid JSON format)
        // This is a more general case than JsonMappingException
        @ExceptionHandler(JsonProcessingException.class)
        public ResponseEntity<ErrorResponse> handleJsonProcessingException(
                        JsonProcessingException ex, HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of(ex.getMessage()));
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle IO exceptions (e.g., file not found)
        // This is a more general case than JsonMappingException
        @ExceptionHandler(IOException.class)
        public ResponseEntity<ErrorResponse> handleIOException(
                        IOException ex, HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of(ex.getMessage()));
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle HTTP message not readable exceptions (e.g., invalid JSON format)
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex, HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of(ex.getMessage()));
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle generic server errors (5xx)
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneralException(
                        Exception ex, HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error",
                                "An unexpected error occurred",
                                request.getRequestURI(),
                                List.of(ex.getMessage()));
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}