#!/bin/bash
# Script should fail fast
set -e

# Function to print colored error messages
print_error() {
  printf "\033[1;31mERROR: %s\033[0m\n" "$1" >&2 # Red
}

# Check if docker-compose or docker compose is installed
if command -v docker-compose &>/dev/null; then
  DOCKER_COMPOSE_COMMAND="docker-compose"
elif docker compose version &>/dev/null; then
  DOCKER_COMPOSE_COMMAND="docker compose"
else
  print_error "Neither 'docker-compose' nor 'docker compose' command found. Please install Docker Compose to proceed."
  exit 1
fi

# Run Docker Compose
$DOCKER_COMPOSE_COMMAND \
  -f docker-compose.yml \
  -f docker-compose-akhq.yml \
  -f docker-compose-repanda-console.yml \
  -f docker-compose-elk.yml \
  -f docker-compose-flink.yml \
  -f docker-compose-kcat.yml \
  --profile full \
  down
