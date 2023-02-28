# Sending without a key
kafka-avro-console-producer --bootstrap-server localhost:9092 --property schema.registry.url=http://localhost:8081 --topic avro-topic \
--property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"f1","type":"string"}]}'

# you can now enter multiple values and submit by hitting enter
# e.g.
# {"f1": "value1-a"}
# {"f1": "value2-b"}
# {"f1": "value3-c"}



# sending with a key
kafka-avro-console-producer --bootstrap-server localhost:9092 --topic t2-a \
  --property parse.key=true \
  --property "key.separator= "\
  --property key.schema='{"type":"string"}' \
  --property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"f1","type":"string"}]}' \
  --property schema.registry.url=http://localhost:8081

# you can now enter multiple values and submit by hitting enter
# e.g.
# "key" {"f1": "value1-a"}
# "key" {"f1": "value2-b"}
# "key" {"f1": "value3-c"}
