#!/bin/bash

# Variables
PROJECT_DIR=$(pwd)  # Current project directory
TARGET_DIR="$PROJECT_DIR/target"  # Target directory

# Build the project with Maven
printf "\n\n[INFO] Building application...\n"
if mvn clean package -DskipTests; then
    echo "Build successful."
else
    echo "Build failed. Exiting."
    exit 1
fi

# Running the tests
printf "\n\n[INFO] Running tests...\n"
if mvn test; then
    echo "Test successful."
else
    echo "Test failed. Exiting."
    exit 1
fi

# Find the generated JAR file
JAR_FILE=$(find "$TARGET_DIR" -type f -name "*.jar" | head -n 1)

# Check if the JAR file exists
if [ -z "$JAR_FILE" ]; then
    echo "Error: No JAR file found in the target directory!"
    exit 1
fi

# Run the JAR file
echo "Running the JAR file: $JAR_FILE"
java -jar "$JAR_FILE"
