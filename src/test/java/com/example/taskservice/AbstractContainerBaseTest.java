package com.example.taskservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class AbstractContainerBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractContainerBaseTest.class);

    // Use singleton pattern to ensure one container instance
    @SuppressWarnings("resource")
    private static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true); // Enable reuse for efficiency

    static {
        // Start container once and keep it running
        MY_SQL_CONTAINER.start();
        logger.info("MySQL Container started at JDBC URL: {}", MY_SQL_CONTAINER.getJdbcUrl());
        // Ensure container is not stopped by Testcontainers
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping MySQL Container");
            MY_SQL_CONTAINER.stop();
        }));
    }

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("spring.datasource.hikari.max-lifetime", () -> "300000");
        registry.add("spring.datasource.hikari.connection-timeout", () -> "30000");
        registry.add("spring.datasource.hikari.idle-timeout", () -> "60000");
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "10");
        registry.add("spring.datasource.hikari.minimum-idle", () -> "2");
        registry.add("spring.datasource.hikari.leak-detection-threshold", () -> "60000");
    }
}