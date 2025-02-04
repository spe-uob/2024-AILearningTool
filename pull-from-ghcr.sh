#!/bin/bash

# Run 'sudo systemctl start docker' before running the script

# Exit script immediately on error
set -e

# Load environment variables from .env file
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
  echo "Error: .env file not found."
  exit 1
fi

# Ensure the GHCR_PAT variable is set
if [ -z "$GHCR_PAT" ]; then
  echo "Error: GHCR_PAT is not set. Check your .env file."
  exit 1
fi

# Variables
GHCR_USERNAME="GerardChabaBristol"  
IMAGE_NAME="ghcr.io/spe-uob/2024-ailearningtool:latest"  

echo "Logging into GHCR..."
echo "$GHCR_PAT" | docker login ghcr.io -u "$GHCR_USERNAME" --password-stdin

echo "Pulling the Docker image..."
docker pull "$IMAGE_NAME"

echo "Docker image pulled successfully: $IMAGE_NAME"
