#!/bin/bash
kafka-avro-console-consumer \
  --bootstrap-server "localhost:9092" \
  --topic "avro-topic" \
  --from-beginning \
  --property "schema.registry.url=http://localhost:8081" \
  --property "print.key=true"
