package com.example.taskservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskservice.dto.TaskRequest;
import com.example.taskservice.dto.TaskResponse;
import com.example.taskservice.mapper.TaskMapper;
import com.example.taskservice.model.Status;
import com.example.taskservice.model.Task;
import com.example.taskservice.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonMergePatch;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Management", description = "APIs for managing tasks")
@SecurityRequirement(name = "BearerAuth")
@Validated
public class TaskController {

    private final TaskService taskService;
    private final ObjectMapper objectMapper;
    private final TaskMapper taskMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String TASKS_TOPIC = "/topic/tasks";

    @Autowired
    private Validator validator;

    public TaskController(TaskService taskService, ObjectMapper objectMapper, TaskMapper taskMapper,
            SimpMessagingTemplate messagingTemplate) {
        this.objectMapper = objectMapper;
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieve a paginated list of tasks with optional filtering and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid token")
    })
    public List<TaskResponse> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String[] sort) {

        Status statusEnum = null;
        if (status != null) {
            statusEnum = Status.valueOf(status.toUpperCase());
        }

        Sort sortOrder = Sort.by(Arrays.stream(sort).map(param -> {
            String[] parts = param.split(",");
            if (parts.length == 2) {
                return new Sort.Order(Sort.Direction.fromString(parts[1]), parts[0]);
            }

            return new Sort.Order(Sort.Direction.ASC, param);
        })
                .filter(order -> !order.getProperty().isEmpty())
                .toArray(Sort.Order[]::new));

        // Create Pageable object for pagination and sorting
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        // Get paginated and filtered results
        if (statusEnum != null) {
            return taskService.getTasksByStatus(statusEnum, pageable).getContent();
        }

        return taskService.getAllTasks(pageable).getContent();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by id", description = "Retrieve data for a specific task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved task"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid token"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @PostMapping
    @Operation(summary = "Create a new task", description = "Creates a new task and returns the created task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid token")
    })
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        TaskResponse taskResponse = taskService.createTask(taskRequest);

        emitWebSocketEvent(taskResponse);

        return ResponseEntity.created(URI.create("/api/tasks/" + taskResponse.getId())).body(taskResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task", description = "Fully updates a task with the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid token"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "409", description = "Conflict due to version mismatch")
    })
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest) {
        TaskResponse updatedTaskResponse = taskService.updateTask(id, taskRequest);

        emitWebSocketEvent(updatedTaskResponse);

        return ResponseEntity.ok(updatedTaskResponse);
    }

    @PatchMapping(path = "/{id}", consumes = "application/merge-patch+json")
    @Operation(summary = "Partially update a task", description = "Updates specific fields of a task using JSON Merge Patch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid token"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "409", description = "Conflict due to version mismatch")
    })
    public ResponseEntity<TaskResponse> partialUpdateTask(@PathVariable Long id,
            @RequestBody JsonMergePatch mergePatch) throws JsonMappingException, JsonProcessingException {

        // Step 1: Retrieve the existing Task
        TaskResponse existingTask = taskService.getTask(id);

        // Step 2: Convert existing Task to JsonNode
        JsonNode targetNode = objectMapper.valueToTree(existingTask);

        // Step 3: Convert JsonNode to JsonValue
        JsonValue target = convertJsonNodeToJsonValue(targetNode);

        // Step 4: Apply the JSON Merge Patch
        JsonValue patched = mergePatch.apply(target);

        // Step 5: Convert JsonValue back to JsonNode
        JsonNode patchedNode = objectMapper.readTree(patched.toString());

        // Step 6: Convert the patched JSON back to a Task
        Task task = objectMapper.convertValue(patchedNode, Task.class);

        // Step 6.1: Validate the Task object
        // Note: This is a safety check to ensure the task object is valid
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new ConstraintViolationException(errorMessage, violations);
        }

        // Step 7: Ensure the ID remains unchanged (safety check)
        task.setId(id);

        TaskRequest taskUpdateRequest = taskMapper.toRequest(task);

        // Step 8: Persist the updated Task
        TaskResponse result = taskService.updateTask(id, taskUpdateRequest);

        emitWebSocketEvent(result);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Deletes a task with the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid token"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);

        emitWebSocketEvent(id);

        return ResponseEntity.noContent().build();
    }

    private JsonValue convertJsonNodeToJsonValue(JsonNode node) {
        if (node.isObject()) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            node.fields().forEachRemaining(entry -> {
                objectBuilder.add(entry.getKey(), convertJsonNodeToJsonValue(entry.getValue()));
            });
            return objectBuilder.build();
        } else if (node.isArray()) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            node.elements().forEachRemaining(element -> arrayBuilder.add(convertJsonNodeToJsonValue(element)));
            return arrayBuilder.build();
        } else if (node.isTextual()) {
            return Json.createValue(node.asText());
        } else if (node.isNumber()) {
            return Json.createValue(node.numberValue().toString());
        } else if (node.isBoolean()) {
            return node.asBoolean() ? JsonValue.TRUE : JsonValue.FALSE;
        } else if (node.isNull()) {
            return JsonValue.NULL;
        }
        throw new IllegalArgumentException("Unsupported JSON node type: " + node.getNodeType());
    }

    private void emitWebSocketEvent(TaskResponse taskResponse) {
        messagingTemplate.convertAndSend(TASKS_TOPIC, taskResponse);
    }

    private void emitWebSocketEvent(Long taskId) {
        messagingTemplate.convertAndSend(TASKS_TOPIC, taskId);
    }

}
