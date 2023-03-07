#!/bin/bash

if [ ! -f "docker-compose.yml" ]; then
  wget https://raw.githubusercontent.com/confluentinc/cp-all-in-one/7.3.0-post/cp-all-in-one/docker-compose.yml
fi
docker-compose  -f docker-compose.yml -f docker-compose-kcat.yml -f docker-compose-akhq.yml  up -d
