package com.example.taskservice.test;

import com.example.taskservice.dto.TaskResponse;
import com.example.taskservice.security.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketStompClientBroadcastTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private CountDownLatch latch;
    private AtomicReference<String> receivedMessage;

    @Autowired
    private JwtUtil jwtUtil;

    private HttpHeaders headers;

    @BeforeEach
    void setUp() throws Exception {
        String token = jwtUtil.generateToken("testuser");
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        stompClient = new WebSocketStompClient(sockJsClient);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setStrictContentTypeMatch(false);
        stompClient.setMessageConverter(converter);

        latch = new CountDownLatch(1);
        receivedMessage = new AtomicReference<>();

        String url = "ws://localhost:" + port + "/ws";
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);

        stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(@NonNull StompSession session, @NonNull StompHeaders connectedHeaders) {
                session.subscribe("/topic/tasks", new StompFrameHandler() {
                    @Override
                    public @NonNull Type getPayloadType(@NonNull StompHeaders headers) {
                        return TaskResponse.class;
                    }

                    @Override
                    public void handleFrame(@NonNull StompHeaders headers, @Nullable Object payload) {
                        TaskResponse taskResponse = (TaskResponse) payload;
                        try {
                            receivedMessage.set(new ObjectMapper().writeValueAsString(taskResponse));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        latch.countDown();
                    }
                });
            }

        }).get(5, TimeUnit.SECONDS);
    }

    @AfterEach
    void tearDown() {
        if (stompSession != null) {
            stompSession.disconnect();
        }
    }

    @Test
    @Transactional
    @Rollback
    void testBroadcastAfterPost() throws Exception {
        ResponseEntity<String> response = this.createTask();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        Long taskId = this.extractIdFromResponse(response);

        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);
        assertEquals(true, messageReceived);
        assertEquals("{\"id\":" + taskId
                + ",\"title\":\"Test Task\",\"description\":\"Test Desc\",\"status\":\"TO_DO\",\"priority\":\"MED\",\"userId\":null}",
                receivedMessage.get());
    }

    @Test
    @Transactional
    @Rollback
    void testBroadcastAfterPut() throws Exception {
        ResponseEntity<String> response = this.createTask();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        Long taskId = this.extractIdFromResponse(response);

        String updateJson = "{\"title\":\"Test Task Updated\",\"description\":\"Test Desc\",\"priority\":\"MED\",\"status\":\"TO_DO\"}";
        HttpEntity<String> updateEntity = new HttpEntity<>(updateJson, headers);
        restTemplate.exchange("/api/tasks/" + taskId, HttpMethod.PUT, updateEntity, String.class);

        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);
        assertEquals(true, messageReceived);
    }

    @Test
    @Transactional
    @Rollback
    void testBroadcastAfterDelete() throws Exception {
        ResponseEntity<String> response = this.createTask();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        Long taskId = this.extractIdFromResponse(response);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange("/api/tasks/" + taskId, HttpMethod.DELETE, entity, String.class);

        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);
        assertEquals(true, messageReceived);
        assertEquals("{\"id\":" + taskId
                + ",\"title\":\"Test Task\",\"description\":\"Test Desc\",\"status\":\"TO_DO\",\"priority\":\"MED\",\"userId\":null}",
                receivedMessage.get());
    }

    @Test
    @Transactional
    @Rollback
    void testBroadcastAfterPatch() throws Exception {
        ResponseEntity<String> response = this.createTask();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        Long taskId = this.extractIdFromResponse(response);

        String updateJson = "{\"description\":\"Partial updated\",\"priority\":\"HIGH\"}";
        headers.set("Content-Type", "application/merge-patch+json");
        HttpEntity<String> updateEntity = new HttpEntity<>(updateJson, headers);
        restTemplate.exchange("/api/tasks/" + taskId, HttpMethod.PATCH, updateEntity, String.class);

        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);
        assertEquals(true, messageReceived);
    }

    private ResponseEntity<String> createTask() {
        String taskJson = "{\"title\":\"Test Task\",\"description\":\"Test Desc\",\"priority\":\"MED\",\"status\":\"TO_DO\"}";
        HttpEntity<String> createEntity = new HttpEntity<>(taskJson, headers);
        return restTemplate.exchange("/api/tasks", HttpMethod.POST, createEntity,
                String.class);
    }

    private Long extractIdFromResponse(ResponseEntity<String> responseBody)
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(
                responseBody.getBody(),
                new TypeReference<Map<String, Object>>() {
                });
        return ((Number) responseMap.get("id")).longValue();
    }
}