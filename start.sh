#!/bin/bash

if [ -f "docker-compose.yml" ]; then
  docker-compose up -d
else
  wget https://raw.githubusercontent.com/confluentinc/cp-all-in-one/7.3.0-post/cp-all-in-one/docker-compose.yml
  docker-compose up -d
fi
