# Kanban board

## Overview
This is a Spring Boot-based RESTful API for a Kanban board application, designed to manage tasks and facilitate real-time updates via WebSocket. The project includes features for task creation, updates, and broadcasting changes to connected clients. It uses Testcontainers for integration testing with a MySQL database.

### Badges
The following badges provide a quick overview of the project status and technologies used. These will render as icons when viewed on GitHub or another Markdown renderer:

[![Build and Test](https://github.com/fgojkovic/kanban-board/actions/workflows/build.yml/badge.svg)](https://github.com/fgojkovic/kanban-board/actions/workflows/build.yml)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-green.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Features
- RESTful endpoints for task management (e.g., create, update, delete).
- Real-time task updates using WebSocket and STOMP messaging.
- Integration with MySQL database using Hibernate/JPA.
- Unit and integration tests with Testcontainers.
- JWT-based authentication for secure access.

## Prerequisites
- Java 21
- Maven 3.8+
- Docker and Docker Compose (for Dockerized setup)
- MySQL 8.0 (optional, for local development)
- Git

## Tehnology
- Spring Boot, springdoc-openapi
- Spring Data JPA, Hibernate.
- Spring Security (JWT filter, BCrypt)
- Spring WebSocket, STOMP, StockJS fallback
- Maven, Docker
- JUnit5, Mockito

## Installation

### Local Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/fgojkovic/Kanban-board.git
   cd Kanban-board
   ```

2. Configure the database (optional for local setup):
   - Create a MySQL database named `testdb`.
   - Update `src/main/resources/application.properties` with your MySQL credentials:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/testdb
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```

3. Build and run the application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. Run tests (requires Docker for Testcontainers):
   ```bash
   mvn verify
   ```

### Dockerized Setup
1. Ensure Docker and Docker Compose are installed and running.
2. Build and start the application using Docker Compose:
   Windows(Using docker desktop)
   ```bash
   docker-compose up --build
   ```
   Linux(Ubuntu)
   ```bash
   docker compose up --build
   ```
   - This will build the application image and start the MySQL container defined in `docker-compose.yml`.
   - The API will be available at `http://localhost:8080`, and the WebSocket at `ws://localhost:8080/ws`.

3. (Optional) View logs for debugging:
   ```bash
   docker-compose logs
   ```

4. Run tests in docker:
    ```bash
    docker-compose up --build test
    ```
5. Stop the containers when done:
   ```bash
   docker-compose down
   ```

## Usage
- The API runs on `http://localhost:8080` by default (both local and Docker setups).
- API requires valid JWT token in the `Authorization` header.
- Use tools like Postman or cURL to interact with REST endpoints (e.g., `POST /api/tasks` to create a task).
- Swagger Endpoint: `http://localhost:8080/swagger-ui/index.html`.
- OpenAPI Endpoint: `http://localhost:8080/v3/api-docs`.
- Actuator health endpoint: `http://localhost:8080/actuator/health`.
- Actuator prometheus endpoint: `http://localhost:8080/actuator/prometheus`.
- WebSocket endpoint: `ws://localhost:8080/ws`.
- WebSocket monitor: `http://localhost:8080/websocket-test.html`.


## API Endpoints
- `POST /api/tasks`: Create a new task.
- `GET /api/tasks`: Retrieve all tasks.
- `PUT /api/tasks/{id}`: Update a task.
- `PATCH /api/tasks/{id}`: Partially update a task.
- `DELETE /api/tasks/{id}`: Delete a task.

## Configuration
- Edit `application.properties` (or environment variables in Docker) to customize ports, database settings, or JWT secrets.
- The `docker-compose.yml` file includes a MySQL service with default settings (e.g., database `testdb`, user `root`, password `password`). Adjust as needed.

## Testing
- Unit tests are located in `src/test/java/com/example/taskservice`.
- Integration tests use Testcontainers to simulate a MySQL environment.
- Run `mvn verify` or use `docker-compose up --build test`.

## Contributing
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/awesome-feature`).
3. Commit your changes (`git commit -m "Add awesome feature"`).
4. Push to the branch (`git push origin feature/awesome-feature`).
5. Open a Pull Request.

## License
No License