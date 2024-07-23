#!/bin/bash
echo "Sending without a key"
echo "You can now enter multiple values and submit by hitting enter"
echo "e.g."
echo '{"f1": "value1-a"}'
echo '{"f1": "value2-b"}'
echo '{"f1": "value3-c"}'

kafka-avro-console-producer \
  --bootstrap-server "localhost:9092" \
  --topic avro-topic \
  --property "schema.registry.url=http://localhost:8081" \
  --property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"f1","type":"string"}]}'

echo "Sending with a key"
echo "You can now enter multiple values and submit by hitting enter"
echo "e.g."
echo '"key" {"f1": "value1-a"}'
echo '"key" {"f1": "value2-b"}'
echo '"key" {"f1": "value3-c"}'

kafka-avro-console-producer \
  --bootstrap-server "localhost:9092" \
  --topic "avro-topic" \
  --property "parse.key=true" \
  --property "key.separator= " \
  --property key.schema='{"type":"string"}' \
  --property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"f1","type":"string"}]}' \
  --property schema.registry.url=http://localhost:8081
