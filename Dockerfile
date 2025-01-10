# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-23-alpine AS builder
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the application source code
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:23.0.1_11-jre-alpine AS prod
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=builder /app/target/*.jar app.jar
# Expose the application port
EXPOSE 8080
# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
