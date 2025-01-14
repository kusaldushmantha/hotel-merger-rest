#!/bin/bash

APP_NAME="kusalk-hotel-merger-api"
DOCKER_IMAGE="$APP_NAME:latest"

printf "\n\n[INFO] Building docker image...\n"
docker build -t $DOCKER_IMAGE .

if [ $? -ne 0 ]; then
  echo "Docker image build failed, exiting."
  exit 1
fi

printf "\n\n[INFO] Running docker image...\n"
docker run -p 8080:8080 $DOCKER_IMAGE

if [ $? -eq 0 ]; then
  echo "Hotel merger API service is running in Docker at http://localhost:8080/api/v1/hotels"
else
  echo "Failed to run the Docker container, exiting."
  exit 1
fi
