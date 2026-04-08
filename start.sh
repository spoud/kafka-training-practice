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

# Function to print usage and available profiles
print_usage() {
  echo "Usage: ./start.sh [PROFILE] [--ui UI]"
  echo ""
  echo "Available profiles:"
  echo "  minimal    (default) broker, schema-registry, kcat"
  echo "  full       everything (connect, ksqldb, elk, flink, all UIs, etc.)"
  echo ""
  echo "Available UI options (--ui):"
  echo "  none       (default) no UI"
  echo "  akhq       AKHQ                      -> http://localhost:8089"
  echo "  redpanda   Redpanda Console          -> http://localhost:8084"
  echo "  confluent  Confluent Control Center  -> http://localhost:9021"
}

# Parse arguments
PROFILE="minimal"
UI="none"

while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help)
      print_usage
      exit 0
      ;;
    --ui)
      if [[ -z "$2" || "$2" == --* ]]; then
        print_error "--ui requires a value"
        echo ""
        print_usage
        exit 1
      fi
      UI="$2"
      shift 2
      ;;
    -*)
      print_error "Unknown option: $1"
      echo ""
      print_usage
      exit 1
      ;;
    *)
      PROFILE="$1"
      shift
      ;;
  esac
done

# Validate profile
if [[ "$PROFILE" != "minimal" && "$PROFILE" != "full" ]]; then
  print_error "Invalid profile: $PROFILE"
  echo ""
  print_usage
  exit 1
fi

# Validate UI
if [[ "$UI" != "none" && "$UI" != "akhq" && "$UI" != "redpanda" && "$UI" != "confluent" ]]; then
  print_error "Invalid UI: $UI"
  echo ""
  print_usage
  exit 1
fi

echo "Starting Docker Compose services with profile: $PROFILE, UI: $UI"

# Build profile flags
PROFILE_FLAGS=(--profile "$PROFILE")
if [[ "$UI" != "none" ]]; then
  PROFILE_FLAGS+=(--profile "$UI")
fi

# Run Docker Compose
$DOCKER_COMPOSE_COMMAND \
  -f docker-compose.yml \
  -f docker-compose-akhq.yml \
  -f docker-compose-repanda-console.yml \
  -f docker-compose-elk.yml \
  -f docker-compose-flink.yml \
  -f docker-compose-kcat.yml \
  "${PROFILE_FLAGS[@]}" \
  up -d
