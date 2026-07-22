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
  echo "Usage: ./start.sh [--full] [--ui UI]"
  echo ""
  echo "Flags:"
  echo "  --full     Also start extra services (connect, ksqldb, elk, flink, all UIs, etc.)"
  echo ""
  echo "Available UI options (--ui):"
  echo "  none       (default) no UI"
  echo "  akhq       AKHQ                      -> http://localhost:8089"
  echo "  redpanda   Redpanda Console          -> http://localhost:8084"
  echo "  confluent  Confluent Control Center  -> http://localhost:9021"
}

# Parse arguments
FULL=false
UI="none"

while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help)
      print_usage
      exit 0
      ;;
    --full)
      FULL=true
      shift
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
    *)
      print_error "Unknown option: $1"
      echo ""
      print_usage
      exit 1
      ;;
  esac
done

# Validate UI
if [[ "$UI" != "none" && "$UI" != "akhq" && "$UI" != "redpanda" && "$UI" != "confluent" ]]; then
  print_error "Invalid UI: $UI"
  echo ""
  print_usage
  exit 1
fi

echo "Starting Docker Compose services (full: $FULL, UI: $UI)"

# Build profile flags
PROFILE_FLAGS=()
if [[ "$FULL" == true ]]; then
  PROFILE_FLAGS+=(--profile full)
fi
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

# Print active URLs
is_running() {
  docker ps --format '{{.Names}}' | grep -q "^${1}$"
}

print_urls() {
  local GREEN="\033[1;32m"
  local CYAN="\033[1;36m"
  local RESET="\033[0m"

  echo ""
  printf "${GREEN}Environment started! Active services:${RESET}\n"
  echo ""
  is_running broker         && printf "  ${CYAN}%-36s${RESET} %s\n" "Kafka bootstrap:"         "localhost:9092"
  is_running broker         && printf "  ${CYAN}%-36s${RESET} %s\n" "Kafka bootstrap from containers:" "broker:29092"
  is_running schema-registry && printf "  ${CYAN}%-36s${RESET} %s\n" "Schema Registry:"         "http://localhost:8081"
  is_running akhq           && printf "  ${CYAN}%-36s${RESET} %s\n" "AKHQ:"                    "http://localhost:8089"
  is_running console        && printf "  ${CYAN}%-36s${RESET} %s\n" "Redpanda Console:"        "http://localhost:8084"
  is_running control-center && printf "  ${CYAN}%-36s${RESET} %s\n" "Confluent Control Center:" "http://localhost:9021"
  is_running rest-proxy     && printf "  ${CYAN}%-36s${RESET} %s\n" "Kafka REST Proxy:"        "http://localhost:8082"
  is_running connect        && printf "  ${CYAN}%-36s${RESET} %s\n" "Kafka Connect:"           "http://localhost:8083"
  is_running ksqldb-server  && printf "  ${CYAN}%-36s${RESET} %s\n" "KsqlDB Server:"           "http://localhost:8088"
  is_running elasticsearch  && printf "  ${CYAN}%-36s${RESET} %s\n" "Elasticsearch:"           "http://localhost:9200"
  is_running kibana         && printf "  ${CYAN}%-36s${RESET} %s\n" "Kibana:"                  "http://localhost:5601"
  is_running jobmanager     && printf "  ${CYAN}%-36s${RESET} %s\n" "Flink Dashboard:"         "http://localhost:8090"
  echo ""
  local YELLOW="\033[1;33m"
  printf "${YELLOW}Useful commands:${RESET}\n"
  echo ""
  printf "  # List topics via the Kafka broker\n"
  printf "  docker exec -it broker kafka-topics --bootstrap-server localhost:9092 --list\n"
  echo ""
  printf "  # List topics via kcat\n"
  printf "  docker exec -it kcat kcat -b broker:29092 -L"
  echo ""
  printf "  # Or enter the kcat container interactively\n"
  printf "  docker exec -it kcat /bin/sh\n"
  echo ""
}

print_urls
