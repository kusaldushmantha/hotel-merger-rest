#!/bin/bash

APP_NAME="hotel-merger-api"
DOCKER_IMAGE="$APP_NAME:latest"

# Step 1: Build the application
printf "\n\n[INFO] Building application...\n"
printf "========================================\n\n"

./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
  echo "Build failed, exiting."
  exit 1
fi

# Step 2: Run tests (if needed)
printf "\n\n[INFO] Running tests...\n"
printf "========================================\n\n"

./mvnw test

if [ $? -ne 0 ]; then
  echo "Tests failed, exiting."
  exit 1
fi

# Step 3: Create a Docker image
printf "\n\n[INFO] Building docker image...\n"
printf "========================================\n\n"

docker build -t $DOCKER_IMAGE .

if [ $? -ne 0 ]; then
  echo "Docker image build failed, exiting."
  exit 1
fi

# Step 4: Run the Docker container
printf "\n\n[INFO] Running docker image...\n"
printf "========================================\n\n"
docker run -p 8080:8080 $DOCKER_IMAGE

if [ $? -eq 0 ]; then
  echo "Spring Boot application is running in Docker at http://localhost:8080"
else
  echo "Failed to run the Docker container, exiting."
  exit 1
fi
