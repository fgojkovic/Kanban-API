# Use a base image with Java 21
FROM openjdk:21-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Copy the Maven build file and wrapper (optional fallback if wrapper is missing)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copy the source code
COPY src ./src

# Debug: List the files to confirm presence
RUN ls -R src
RUN ls -l mvnw .mvn || echo "Maven wrapper files not found, falling back to installed Maven"

# Ensure the Maven wrapper is executable (if present)
RUN chmod +x mvnw 2>/dev/null || echo "No mvnw to chmod"

# Install Maven and build the application
RUN apt-get update && apt-get install -y maven && \
    if [ -f "./mvnw" ]; then ./mvnw clean package -DskipTests; else mvn clean package -DskipTests; fi

# Use a smaller base image for runtime
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/kanban-board-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]